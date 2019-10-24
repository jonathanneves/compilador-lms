package core;

public class AnalisadorSemantico {

	private Hipotetica maquinaVirtual;
	private TabelaDeSimbolos tabelaDeSimbolos;
	
	public AnalisadorSemantico() {
	}
	
	public void executarSemantico(int codigoDaAcaoSemantica) {
		System.out.println("A��o Sem�ntica n�mero #"+codigoDaAcaoSemantica +" executado!");
		
		switch (codigoDaAcaoSemantica) {
			case 100:
				this.maquinaVirtual = new Hipotetica();
				this.tabelaDeSimbolos = new TabelaDeSimbolos(25147);
				break;
			case 102:
				break;
			case 130:
				break;
		}
	}
}
