package enums;

public enum InstrucaoEnum {
	
	RETU(1), // retorno de procedure.
	CRVL(2), // carrega valor na pilha.
	CRCT(3), // carrega constante na pilha.
	ARMZ(4), // armazena conte�do da pilha(topo) no endere�o dado.
	SOMA(5), // opera��o soma com elementos do topo e sub-topo.
	SUBT(6), // opera��o de subtra��o.
	MULT(7), // opera��o de multiplica��o.
	DIVI(8), // opera��o de divis�o.
	INVR(9), // inverte sinal.
	NEGA(10), // opera��o de nega��o.
	CONJ(11), // opera��o AND.
	DISJ(12), // opera��o de OR.
	CMME(13), // compara menor.
	CMMA(14), // compara maior.
	CMIG(15), // compara igual.
	CMDF(16), // compara diferente.
	CMEI(17), // compara menor igual.
	CMAI(18), // compara maior igual.
	DSVS(19), // desviar sempre.
	DSVF(20), // desviar se falso.
	LEIT(21), // leitura.
	IMPR(22), // imprimir topo da pilha.
	IMPRL(23), // imprimir literal extra�do da �rea de literais.
	AMEM(24), // alocar espa�o na �rea de dados.
	CALL(25), // chamada de procedura �a� no n�vel �l�.
	PARA(26), // finaliza a execu��o.
	NADA(27), // nada faz, continua a execu��o.
	COPI(28), // duplica o topo da pilha
	DSVT(29); // desvia se verdadeiro.
	
	private int codigo;
	
	private InstrucaoEnum(Integer codigo) {
		this.codigo = codigo;
	}

	public Integer getCodigo() {
		return codigo;
	}
}
