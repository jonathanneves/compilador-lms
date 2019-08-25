package analisadores;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import constants.Constants;
import models.Token;

public class AnalisadorSintatico implements Constants {
	
	//Inicia a an�lise utilizando os tokens do l�xico
	public static String iniciarAnalise(List<Token> tokens) {
		return iniciarDescendentePreditivo(tokens);
	}
	
	//Iniciar a An�lise Sint�tica Descendente
	public static String iniciarDescendentePreditivo(List<Token> entrada) {
		//Cria��o da pilha
		List<Integer> pilha = new ArrayList<Integer>();
		List<Integer> fila = preencherFila(entrada);

		int topo_da_pilha;
		int proxima_entrada;
		//Adicionando o token inicial a ela
		pilha.add(Constants.DOLLAR);
		pilha.add(Constants.START_SYMBOL);
		//Variavel para o valor retornado da matriz de parsing
		int valorMatriz;
		
		System.out.println("INICIANDO TESTE");
		
		//In�cio da an�lise Descendente Preditivo at� a pilha ficar vazia
		
		do {
			//Setando o topo_da_pilha e a proxima_entrada baseado na lista de tokens.
			topo_da_pilha = pilha.get(pilha.size()-1);
			proxima_entrada = fila.get(0);
			System.out.println(topo_da_pilha + " " + proxima_entrada + " " +entrada.size());
			
			//Verifica se o token no Topo da Pilha � um terminal ou est� vazio, caso contr�rio encessa o processo
			if(topo_da_pilha < Constants.FIRST_NON_TERMINAL || topo_da_pilha == Constants.DOLLAR) {
				//Verifica se o Topo Da Pilha � igual ao proximo s�mbolo da entrada
				
				if(topo_da_pilha == proxima_entrada) {
					//Se for remove a s�mbolo do topo da pilha
					pilha.remove(pilha.size()-1);
					fila.remove(0);
				} else {
					//Se for diferente erro sint�tico
					return "ERRO SINT�TICO: "+ Constants.PARSER_ERROR[topo_da_pilha] + " na linha "+entrada.get(entrada.size() - fila.size()).getLinha();
				}
			} else {
				//Baseado na Matriz de Parse ele pega:
				//linha = topo_da_pilha - Primeiro N�o terminal
				//coluna = proxima_entrada - 1;
				valorMatriz = Constants.PARSER_TABLE[topo_da_pilha-Constants.FIRST_NON_TERMINAL][proxima_entrada-1];

				//Se valor retornado for igual topo_da_pilha
				if(valorMatriz != -1) {
					//Remove da pilha e entrada	
					pilha.remove(pilha.size()-1);
					//Adiciona na pilha as produ��es em ordem decrescente
					for(int j=Constants.PRODUCTIONS[valorMatriz].length-1; j>=0; j--) {
						//0 � vazio ent�o n�o adiciona
						if(Constants.PRODUCTIONS[valorMatriz][j] != 0) {
							//Remove da pilha e entrada											
							//Adiciona novos tokens da produ��o a pilha
							pilha.add(Constants.PRODUCTIONS[valorMatriz][j]);
						}
					}
				} else {
						//Se for diferente ERRO sint�tico
					return "ERRO SINT�TICO: "+ Constants.PARSER_ERROR[topo_da_pilha] + " na linha "+entrada.get(entrada.size() - fila.size()).getLinha();
				}
			}
		} while (!fila.isEmpty());  //(topo_da_pilha != Constants.DOLLAR);
		
		return "";
	}
	
	private static List<Integer> preencherFila(List<Token> entrada){
		List<Integer> fila = entrada.stream().map(Token::getCodigo).collect(Collectors.toList());
		for(Integer f : fila)
			System.out.println(f);
		return fila;
	}
}

