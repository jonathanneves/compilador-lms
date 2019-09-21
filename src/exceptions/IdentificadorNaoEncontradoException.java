package exceptions;

public class IdentificadorNaoEncontradoException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public IdentificadorNaoEncontradoException(String nome) {
		super(String.format("O identificador %s n�o foi encontrado", nome));
	}
}
