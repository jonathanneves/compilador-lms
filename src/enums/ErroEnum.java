package enums;

public enum ErroEnum {
	
	INTEIRO_FORA_DO_LIMITE("ERRO L�XICO: INTEIRO FORA DO LIMITE - N�mero inteiro � maior que 32767 ou menor que -32767"), 
	CARACTERES_FORA_DO_LIMITE("ERRO L�XICO: CARACTERES FORA DO LIMITE - Sequ�ncia de caracteres acima de 255 de caracteres"),
	IDENTIFICADOR_FORA_DO_LIMITE("ERRO L�XICO: IDENTIFICADOR FORA DO LIMITE - Vari�vel deve possuir no m�ximo 30 caracteres"),
	IDENTIFICADOR_DECLARADO_ERRADO("ERRO L�XICO: IDENTIFICADOR DECLARADO ERRADO - Vari�vel n�o pode iniciar com um n�mero");

	private String erro;

	private ErroEnum(String erro) {
		this.erro = erro;
	}

	public String getErro() {
		return erro;
	}

}
