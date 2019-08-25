package analisadores;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import constants.Constants;
import models.Token;

public class AnalisadorSintatico implements Constants {
	
	private List<Integer> pilha;
	private List<Integer> fila;
	private List<Token> tokens;
	
	public AnalisadorSintatico(List<Token> tokens) {
		// Cria a pilha e a fila
		this.pilha = new ArrayList<>();
		this.fila = new ArrayList<>();
		this.tokens = tokens;
	}

	// Inicia a an�lise utilizando os tokens do l�xico
	public String iniciarAnalise() {
		return iniciarDescendentePreditivo();
	}
	
	// Inicia a An�lise Sint�tica Descendente
	private String iniciarDescendentePreditivo() {
		this.preencherFila();

		int topoDaPilha;
		int proximaEntrada;
		
		// Adiciona o token inicial � pilha
		this.pilha.add(Constants.DOLLAR);
		this.pilha.add(Constants.START_SYMBOL);
		
		//Variavel para o valor retornado da matriz de parsing
		int valorMatriz;
		
		System.out.println("INICIANDO TESTE");
		
		// In�cio da an�lise Descendente Preditivo at� a pilha ficar vazia
		
		do {
			// Setando o topoDaPilha e a proximaEntrada baseado na lista de tokens.
			topoDaPilha = this.pilha.get(pilha.size() - 1);
			proximaEntrada = this.fila.get(0);
			System.out.println(topoDaPilha + " " + proximaEntrada + " " + this.tokens.size());
			
			// Verifica se o token no Topo da Pilha � um terminal ou est� vazio, caso contr�rio encessa o processo
			if(topoDaPilha < Constants.FIRST_NON_TERMINAL || topoDaPilha == Constants.DOLLAR) {
				// Verifica se o Topo Da Pilha � igual ao proximo s�mbolo da entrada
				
				if(topoDaPilha == proximaEntrada) {
					// Se for remove a s�mbolo do topo da pilha
					this.pilha.remove(this.pilha.size() - 1);
					this.fila.remove(0);
				} else {
					//Se for diferente erro sint�tico
					return "ERRO SINT�TICO: " + Constants.PARSER_ERROR[topoDaPilha] + " na linha " + this.tokens.get(this.tokens.size() - this.fila.size()).getLinha();
				}
			} else {
				// Baseado na Matriz de Parse ele pega:
				// linha = topoDaPilha - Primeiro N�o terminal
				// coluna = proximaEntrada - 1;
				valorMatriz = Constants.PARSER_TABLE[topoDaPilha-Constants.FIRST_NON_TERMINAL][proximaEntrada - 1];

				// Se valor retornado for igual topoDaPilha
				if(valorMatriz != -1) {
					// Remove da pilha e entrada	
					this.pilha.remove(pilha.size()-1);
					// Adiciona na pilha as produ��es em ordem decrescente
					for(int j = Constants.PRODUCTIONS[valorMatriz].length-1; j >= 0; j--) {
						// 0 � vazio ent�o n�o adiciona
						if(Constants.PRODUCTIONS[valorMatriz][j] != 0) {
							// Remove da pilha e entrada											
							// Adiciona novos tokens da produ��o a pilha
							this.pilha.add(Constants.PRODUCTIONS[valorMatriz][j]);
						}
					}
				} else {
						// Se for diferente ERRO sint�tico
					return "ERRO SINT�TICO: "+ Constants.PARSER_ERROR[topoDaPilha] + " na linha " + this.tokens.get(this.tokens.size() - this.fila.size()).getLinha();
				}
			}
		} while (!this.fila.isEmpty());  // (topoDaPilha != Constants.DOLLAR);
		
		return "";
	}
	
	private void preencherFila(){
		this.fila = this.tokens.stream().map(Token::getCodigo).collect(Collectors.toList());
		for(Integer f : fila) System.out.println(f);
	}
}

