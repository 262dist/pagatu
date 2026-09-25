package pe.edu.upeu.auth.exception;

public class EmailYaRegistradoException extends RuntimeException {
    public EmailYaRegistradoException(String email) {
        super("El email ya está registrado: " + email);
    }
}
