package com.uniquindio.ecommerce.infrastructure.rest.exception;

import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.infrastructure.security.CorreoYaRegistradoException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Traduce las excepciones que llegan a cualquier controller en una respuesta JSON
 * con el codigo HTTP correcto:
 * 400 datos invalidos, 401 login fallido, 404 el recurso no existe, 409 se rompio una regla de negocio.
 * (El 401 sin token y el 403 por rol los responde ManejadorErroresSeguridad, antes de llegar aqui.)
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** Una regla del dominio se rompio (sin stock, publicacion retirada, pedido enviado...). */
    @ExceptionHandler(ReglaDominioException.class)
    public ResponseEntity<Map<String, Object>> manejarReglaDominio(ReglaDominioException ex) {
        return construirRespuesta(HttpStatus.CONFLICT, ex.getMessage(), null); // 409
    }

    /** El pedido o la publicacion pedida no existe. */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, Object>> manejarNoEncontrado(NoSuchElementException ex) {
        String mensaje = ex.getMessage() != null ? ex.getMessage() : "El recurso solicitado no existe";
        return construirRespuesta(HttpStatus.NOT_FOUND, mensaje, null); // 404
    }

    /** El correo o la contrasena del login no coinciden. */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> manejarLoginFallido(AuthenticationException ex) {
        return construirRespuesta(HttpStatus.UNAUTHORIZED, "Correo o contrasena incorrectos", null); // 401
    }

    /** Se intento registrar un correo que ya tiene cuenta. */
    @ExceptionHandler(CorreoYaRegistradoException.class)
    public ResponseEntity<Map<String, Object>> manejarCorreoRepetido(CorreoYaRegistradoException ex) {
        return construirRespuesta(HttpStatus.CONFLICT, ex.getMessage(), null); // 409
    }

    /** Una entrada que valida el caso de uso no es valida (pagina negativa, tamano fuera de rango...). */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> manejarEntradaInvalida(IllegalArgumentException ex) {
        return construirRespuesta(HttpStatus.BAD_REQUEST, ex.getMessage(), null); // 400
    }

    /** Un campo del body no cumple su validacion (@NotBlank, @Positive...). */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> manejarBodyInvalido(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errores.putIfAbsent(error.getField(), error.getDefaultMessage()));
        return construirRespuesta(HttpStatus.BAD_REQUEST, "Hay datos invalidos en la peticion", errores); // 400
    }

    /** Un parametro de la URL no cumple su validacion (por ejemplo, el tamano de pagina). */
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<Map<String, Object>> manejarParametroInvalido(HandlerMethodValidationException ex) {
        Map<String, String> errores = new LinkedHashMap<>();
        ex.getParameterValidationResults().forEach(resultado -> {
            String parametro = resultado.getMethodParameter().getParameterName();
            resultado.getResolvableErrors().forEach(error -> errores.putIfAbsent(parametro, error.getDefaultMessage()));
        });
        return construirRespuesta(HttpStatus.BAD_REQUEST, "Hay parametros invalidos en la peticion", errores); // 400
    }

    /** Un parametro tiene el tipo equivocado (un id que no es UUID, un numero con letras...). */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> manejarTipoInvalido(MethodArgumentTypeMismatchException ex) {
        return construirRespuesta(HttpStatus.BAD_REQUEST,
                "El valor '" + ex.getValue() + "' no es valido para '" + ex.getName() + "'", null); // 400
    }

    /** Falta un parametro obligatorio en la URL. */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> manejarParametroFaltante(MissingServletRequestParameterException ex) {
        return construirRespuesta(HttpStatus.BAD_REQUEST,
                "Falta el parametro obligatorio '" + ex.getParameterName() + "'", null); // 400
    }

    /** El body no es un JSON valido o tiene un tipo de dato equivocado. */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> manejarJsonInvalido(HttpMessageNotReadableException ex) {
        return construirRespuesta(HttpStatus.BAD_REQUEST, "El cuerpo de la peticion no es un JSON valido", null); // 400
    }

    private ResponseEntity<Map<String, Object>> construirRespuesta(HttpStatus status, String mensaje,
                                                                   Map<String, String> errores) {
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("timestamp", LocalDateTime.now().toString());
        cuerpo.put("status", status.value());
        cuerpo.put("error", status.getReasonPhrase());
        cuerpo.put("mensaje", mensaje);
        if (errores != null && !errores.isEmpty()) {
            cuerpo.put("errores", errores);
        }
        return ResponseEntity.status(status).body(cuerpo);
    }
}
