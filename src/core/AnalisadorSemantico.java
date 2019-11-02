package core;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import constants.Constants;
import enums.CategoriaSimboloEnum;
import enums.ContextoEnum;
import enums.InstrucaoEnum;
import exceptions.AnalisadorSemanticoException;
import exceptions.SimboloNaoEncontradoException;
import hipotetica.AreaInstrucoes;
import hipotetica.AreaLiterais;
import hipotetica.Hipotetica;
import hipotetica.Tipos;
import models.Literal;
import models.Simbolo;
import models.Token;

public class AnalisadorSemantico {

	private Hipotetica maquinaVirtual;
	private TabelaDeSimbolos tabelaDeSimbolos;
	private AreaInstrucoes areaInstrucoes;
	private AreaLiterais areaLiterais;
	
	private List<Integer> pilhaIf;
	private List<Integer> pilhaWhile;
	private List<Integer> pilhaRepeat;
	private List<Integer> pilhaProcedure;
	private List<Integer> pilhaCase;
	private List<Integer> pilhaFor;
	
	private Integer nivelAtual;
	private Integer posicaoLivre;
	private Integer numeroVariaveis;
	private Integer numeroParametros;
	private Integer deslocamento;
	private Integer ponteiroLit; 
	private Integer[] escopo = new Integer[100]; // Verificar posteriormente o tamanho desse vetor e o prop�sito dele
	private CategoriaSimboloEnum tipoIdentificador;
	private ContextoEnum contexto;
	private Simbolo variavelDeAtribuicao;
	private Simbolo novaConstante;
	
	public AnalisadorSemantico() {
		this.maquinaVirtual = new Hipotetica();
		this.areaInstrucoes = new AreaInstrucoes();
		this.areaLiterais = new AreaLiterais();
	}
	
	public void interpretarMaquinaVirtual() {
		System.out.println("M�quina Virtual interpretada");
		Hipotetica.Interpreta(this.areaInstrucoes, this.areaLiterais);
	}
	
	public void executarSemantico(int codigoDaAcaoSemantica, Token tokenAnterior) {
		switch (codigoDaAcaoSemantica) {
//			Reconhecendo o nome do programa
			case 100:
				inicializaPilhas();
				this.tabelaDeSimbolos = new TabelaDeSimbolos(25147);
				Hipotetica.InicializaAI(this.areaInstrucoes);
				Hipotetica.InicializaAL(this.areaLiterais);
				inicializaVariaveis();
				break;
				
//			Final de programa
			case 101:
				maquinaVirtual.IncluirAI(this.areaInstrucoes, InstrucaoEnum.PARA.getCodigo(), Constants.VAZIO, Constants.VAZIO);
				break;
				
//			Ap�s declara��o de vari�vel
			case 102:
				maquinaVirtual.IncluirAI(this.areaInstrucoes, InstrucaoEnum.AMEM.getCodigo(), Constants.VAZIO, numeroVariaveis + deslocamento);
				break;
				
//			Encontrado o nome de r�tulo, de vari�vel, ou de par�metro de procedure em declara��o
			case 104:
				if (tabelaDeSimbolos.existe(tokenAnterior.getToken(), nivelAtual)) {
					throw new AnalisadorSemanticoException(
							String.format("Erro sem�ntico na linha %s: o simbolo %s j� foi declarado",
									tokenAnterior.getLinha().toString(), tokenAnterior.getToken()));
				}
				if (tipoIdentificador.equals(CategoriaSimboloEnum.VARIAVEL)) {
					tabelaDeSimbolos.inserir(new Simbolo(tokenAnterior.getToken(), CategoriaSimboloEnum.VARIAVEL, nivelAtual, deslocamento + numeroVariaveis, null));
					numeroVariaveis++;
				} else if (tipoIdentificador.equals(CategoriaSimboloEnum.PARAMETRO)) {
					tabelaDeSimbolos.inserir(new Simbolo(tokenAnterior.getToken(), CategoriaSimboloEnum.PARAMETRO, nivelAtual, deslocamento + numeroParametros, null));
					numeroParametros++;
				}
				break;

//			Reconhecido nome de constante em declara��o 
			case 105:
				if (tabelaDeSimbolos.existe(tokenAnterior.getToken(), nivelAtual)) {	
					throw new AnalisadorSemanticoException(
						String.format("Erro sem�ntico na linha %s: o simbolo %s j� foi declarado",
								tokenAnterior.getLinha().toString(), tokenAnterior.getToken()));
				} else {
					novaConstante = new Simbolo(tokenAnterior.getToken(), CategoriaSimboloEnum.CONSTANTE, nivelAtual, null, null);
					tabelaDeSimbolos.inserir(novaConstante);
				}
				break;
		
// 			Reconhecido valor de constante em declara��o
			case 106:
				novaConstante.setGeralA(Integer.parseInt(tokenAnterior.getToken()));
				break;
						
//			Antes de lista de identificadores em declara��o de vari�veis	
			case 107:
				tipoIdentificador = CategoriaSimboloEnum.VARIAVEL;
				break;
				
//			Atribui��o parte esquerda 				
			case 114:
				try {
					Simbolo simbolo = tabelaDeSimbolos.buscar(tokenAnterior.getToken(), nivelAtual);
					if(simbolo.getCategoria() != CategoriaSimboloEnum.VARIAVEL)	{
						throw new AnalisadorSemanticoException(
								String.format("Erro sem�ntico na linha %s: o simbolo %s n�o � uma vari�vel",
										tokenAnterior.getLinha().toString(), tokenAnterior.getToken()));
					} else {
						variavelDeAtribuicao = simbolo;
					}
				} catch (SimboloNaoEncontradoException e) {
					   String.format("Erro sem�ntico na linha %s: o simbolo %s n�o existe",
					     tokenAnterior.getLinha().toString(), tokenAnterior.getToken());
				}
				break;

// 			Ap�s express�o em atribui��o
			case 115:
				maquinaVirtual.IncluirAI(this.areaInstrucoes, InstrucaoEnum.ARMZ.getCodigo(), variavelDeAtribuicao.getNivel(), variavelDeAtribuicao.getGeralA());
				break;
				
//			Ap�s express�o num comando IF	
			case 120:
				maquinaVirtual.IncluirAI(this.areaInstrucoes, InstrucaoEnum.DSVF.getCodigo(), Constants.VAZIO, Constants.VAZIO);
				this.pilhaIf.add(maquinaVirtual.enderecoProximaInstrucao - 1);
				break;
			
//			Ap�s instru��o IF
			case 121:
				int enderecoDSVS = this.getTopoDaPilha(this.pilhaIf);
				this.getInstrucaoByEndereco(enderecoDSVS).op2 = maquinaVirtual.enderecoProximaInstrucao;
				break;
			
//			Ap�s dom�nio do THEN, antes do ELSE
			case 122:
				int enderecoDSVF = this.getTopoDaPilha(this.pilhaIf);
				int proximoEndereco = maquinaVirtual.enderecoProximaInstrucao + 1;
				this.getInstrucaoByEndereco(enderecoDSVF).op2 = proximoEndereco;
				
				maquinaVirtual.IncluirAI(this.areaInstrucoes, InstrucaoEnum.DSVS.getCodigo(), Constants.VAZIO, Constants.VAZIO);
				this.pilhaIf.add(maquinaVirtual.enderecoProximaInstrucao - 1);
				break;
				
//			Comando READLN in�cio
			case 128:
				contexto = ContextoEnum.READLN;
				break;
							
//			Identificador de vari�vel
			case 129:
				try {
					Simbolo simbolo = tabelaDeSimbolos.buscar(tokenAnterior.getToken(), nivelAtual);
					if (contexto == ContextoEnum.READLN) {
						maquinaVirtual.IncluirAI(this.areaInstrucoes, InstrucaoEnum.LEIT.getCodigo(), Constants.VAZIO, Constants.VAZIO);
						int deslocamentoDoToken = simbolo.getGeralA();
						maquinaVirtual.IncluirAI(this.areaInstrucoes, InstrucaoEnum.ARMZ.getCodigo(), nivelAtual, deslocamentoDoToken);
					} else if (contexto == ContextoEnum.EXPRESSAO) {
						if (simbolo.getCategoria() == CategoriaSimboloEnum.PROCEDURE) {
							throw new AnalisadorSemanticoException(
									String.format("Erro sem�ntico na linha %s: o simbolo %s � um Procedure",
											tokenAnterior.getLinha().toString(), tokenAnterior.getToken()));
						} else if (simbolo.getCategoria() == CategoriaSimboloEnum.CONSTANTE){ 
							int valorDecimal = simbolo.getGeralA();
							maquinaVirtual.IncluirAI(this.areaInstrucoes, InstrucaoEnum.CRCT.getCodigo(), Constants.VAZIO, valorDecimal);
						} else {
							int deslocamentoDoToken = simbolo.getGeralA();
							maquinaVirtual.IncluirAI(this.areaInstrucoes, InstrucaoEnum.CRVL.getCodigo(), nivelAtual, deslocamentoDoToken);
						}
					}
				} catch (SimboloNaoEncontradoException e) {
					throw new AnalisadorSemanticoException(
							String.format("Erro sem�ntico na linha %s: o simbolo %s n�o foi declarado",
									tokenAnterior.getLinha().toString(), tokenAnterior.getToken()));
				}
				break;
					
//			WRITELN - ap�s literal na instru��o WRITELN
			case 130:
				maquinaVirtual.IncluirAL(this.areaLiterais, tokenAnterior.getToken());
				maquinaVirtual.IncluirAI(this.areaInstrucoes, InstrucaoEnum.IMPRL.getCodigo(), Constants.VAZIO, ponteiroLit);
				ponteiroLit++;
				break;
				
//			WRITELN ap�s express�o
			case 131:
				maquinaVirtual.IncluirAI(this.areaInstrucoes, InstrucaoEnum.IMPR.getCodigo(), Constants.VAZIO, Constants.VAZIO);
				break;
				
//			Compara��o - igual
			case 141:
				maquinaVirtual.IncluirAI(this.areaInstrucoes, InstrucaoEnum.CMIG.getCodigo(), Constants.VAZIO, Constants.VAZIO);
				break;
			
//			Compara��o - menor
			case 142:
				maquinaVirtual.IncluirAI(this.areaInstrucoes, InstrucaoEnum.CMME.getCodigo(), Constants.VAZIO, Constants.VAZIO);
				break;
				
//			Compara��o - maior
			case 143:
				maquinaVirtual.IncluirAI(this.areaInstrucoes, InstrucaoEnum.CMMA.getCodigo(), Constants.VAZIO, Constants.VAZIO);
				break;
			
//			Compara��o - maior ou igual
			case 144:
				maquinaVirtual.IncluirAI(this.areaInstrucoes, InstrucaoEnum.CMAI.getCodigo(), Constants.VAZIO, Constants.VAZIO);
				break;
				
//			Compara��o - menor ou igual
			case 145:
				maquinaVirtual.IncluirAI(this.areaInstrucoes, InstrucaoEnum.CMEI.getCodigo(), Constants.VAZIO, Constants.VAZIO);
				break;
				
//			Compara��o - diferente
			case 146:
				maquinaVirtual.IncluirAI(this.areaInstrucoes, InstrucaoEnum.CMDF.getCodigo(), Constants.VAZIO, Constants.VAZIO);
				break;
				
//			Express�o � soma
			case 148:
				maquinaVirtual.IncluirAI(this.areaInstrucoes, InstrucaoEnum.SOMA.getCodigo(), Constants.VAZIO, Constants.VAZIO);
				break;
				
//			Express�o � subtra��o
			case 149:
				maquinaVirtual.IncluirAI(this.areaInstrucoes, InstrucaoEnum.SUBT.getCodigo(), Constants.VAZIO, Constants.VAZIO);
				break;
				
//			Express�o � multiplica��o
			case 151:
				maquinaVirtual.IncluirAI(this.areaInstrucoes, InstrucaoEnum.MULT.getCodigo(), Constants.VAZIO, Constants.VAZIO);
				break;
				
//			Express�o � divis�o
			case 152:
				maquinaVirtual.IncluirAI(this.areaInstrucoes, InstrucaoEnum.DIVI.getCodigo(), Constants.VAZIO, Constants.VAZIO);
				break;
				
//			Express�o � inteiro
			case 154:
				maquinaVirtual.IncluirAI(this.areaInstrucoes, InstrucaoEnum.CRCT.getCodigo(), Constants.VAZIO, Integer.parseInt(tokenAnterior.getToken()));
				break;
			
//			Express�o - vari�vel
			case 156:
				contexto = ContextoEnum.EXPRESSAO;
				break;
				
			default:
				System.out.println("A��o Sem�ntica n�mero " + codigoDaAcaoSemantica + " n�o implementada");
		}
	}
	
	public List<Tipos> obterInstrucoes() {
		List<Tipos> lista = Arrays.asList(this.areaInstrucoes.AI).stream().filter(item -> item.codigo != Constants.VAZIO).collect(Collectors.toList());
		int endereco = 0;
		for (Tipos instrucao : lista) {
			instrucao.endereco = endereco;
			endereco++;
		}
		return lista;
	}
	
	public List<Literal> obterLiterais() {
		List<String> lista = Arrays.asList(this.areaLiterais.AL).stream().filter(item -> !item.isEmpty()).collect(Collectors.toList());
		List<Literal> literais = new ArrayList<>();
		int endereco = 0;
		for (String literal : lista) {
			literais.add(new Literal(endereco, literal));
			endereco++;
		}
		return literais;
	}
	
	private int getTopoDaPilha(List<Integer> pilha) {
		return pilha.get(pilha.size() - 1);
	}
	
	private Tipos getInstrucaoByEndereco(int endereco) {
		return this.obterInstrucoes().stream().filter(instrucao -> instrucao.endereco == endereco).findFirst().orElse(null);
	}
	
	private void inicializaPilhas() {
		this.pilhaIf = new ArrayList<>();
		this.pilhaWhile = new ArrayList<>();
		this.pilhaRepeat = new ArrayList<>();
		this.pilhaProcedure = new ArrayList<>();
		this.pilhaCase = new ArrayList<>();
		this.pilhaFor = new ArrayList<>();
	}
	
	private void inicializaVariaveis() {
		this.nivelAtual = 0;
		this.posicaoLivre = 1;
		this.escopo[0] = 1;
		this.numeroVariaveis = 0;
		this.numeroParametros = 0;
		this.deslocamento = 3;
		this.ponteiroLit = 0;
	}
}
