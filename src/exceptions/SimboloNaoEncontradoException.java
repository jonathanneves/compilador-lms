package exceptions;

public class SimboloNaoEncontradoException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public SimboloNaoEncontradoException(String nome) {
		super(String.format("O s�mbolo %s n�o foi encontrado", nome));
	}
}
