package exceptions;

public class AnalisadorSintaticoException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public AnalisadorSintaticoException(String erro, Integer linha) {
		super("ERRO SINT�TICO: " + erro + " na linha " + linha);
	}
	
}
