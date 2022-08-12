package com.usuario.service.controlador;




import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.usuario.service.entidades.Usuario;
import com.usuario.service.modelos.Carro;
import com.usuario.service.modelos.Moto;

import com.usuario.service.servicio.UsuarioService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {
	@Autowired
	private UsuarioService usuarioService;
	@GetMapping
	public ResponseEntity<List<Usuario>> listarUsuarios(){
		List<Usuario> usuarios =usuarioService.getAll();
		if (usuarios.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(usuarios);
	}
	@GetMapping("/{id}")
	public ResponseEntity<Usuario> obtenerUsuario(@PathVariable("id") Long id){
		Usuario usuario =usuarioService.getUsuarioById(id);
		if (usuario==null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(usuario);
	}
	
	@PostMapping
	public ResponseEntity<Usuario> guardarUsuario(@RequestBody Usuario usuario){
		Usuario nuevoUsuario = usuarioService.save(usuario);
		return ResponseEntity.ok(nuevoUsuario);
	}
	@CircuitBreaker(name= "carroCB", fallbackMethod = "fallBackGetCarros")
	@GetMapping("/carros/{usuarioId}")
	public ResponseEntity<List<Carro>> listarCarros(@PathVariable("usuarioId") Long usuarioId)
	{
		Usuario usuario = usuarioService.getUsuarioById(usuarioId);
		if (usuario==null) {
			return ResponseEntity.noContent().build();
		}
		List<Carro> carros = usuarioService.getCarros(usuarioId);
		return ResponseEntity.ok(carros);
	}
	@CircuitBreaker(name= "motoCB", fallbackMethod = "fallBackGetMotos")
	@GetMapping("/motos/{usuarioId}")
	public ResponseEntity<List<Moto>> listarMotos(@PathVariable("usuarioId") Long usuarioId)
	{
		Usuario usuario = usuarioService.getUsuarioById(usuarioId);
		if (usuario==null) {
			return ResponseEntity.noContent().build();
		}
		List<Moto> motos = usuarioService.getMotos(usuarioId);
		return ResponseEntity.ok(motos);
	}
	
	@CircuitBreaker(name= "carroCB", fallbackMethod = "fallBackSaveCarro")
	@PostMapping("/carro/{usuarioId}")
	public ResponseEntity<Carro> guardarCarro(@PathVariable("usuarioId") Long usuarioId,@RequestBody Carro carro){
		Carro nuevoCarro=usuarioService.saveCarro(usuarioId, carro);
		return ResponseEntity.ok(nuevoCarro);
	}
	@CircuitBreaker(name= "motoCB", fallbackMethod = "fallBackSaveMoto")
	@PostMapping("/moto/{usuarioId}")
	public ResponseEntity<Moto> guardarMoto(@PathVariable("usuarioId") Long usuarioId,@RequestBody Moto moto){
		Moto nuevaMoto=usuarioService.saveMoto(usuarioId, moto);
		return ResponseEntity.ok(nuevaMoto);
	}
	@CircuitBreaker(name= "todosCB", fallbackMethod = "fallBackGetTodos")
	@GetMapping("/todos/{usuarioId}")
	public ResponseEntity<Map<String,Object>> listarTodosLosVehiculos(@PathVariable("usuarioId") Long usuarioId){
		Map<String,Object> resultado =usuarioService.getUsuarioAndVehiculos(usuarioId);
		return ResponseEntity.ok(resultado);
	}
	
	
	//METODOS DE CIRCUIT BREAKER PARA LOS FALLOS
	private ResponseEntity<List<Carro>> fallBackGetCarros(@PathVariable ("usuarioId") Long id,RuntimeException excepcion){
		return new ResponseEntity("El usuario : "+id +" tiene los carros en el taller",HttpStatus.OK);
	}
	
	private ResponseEntity<Carro> fallBackSaveCarro(@PathVariable ("usuarioId") Long id,RuntimeException excepcion,@RequestBody Carro carro){
		return new ResponseEntity("El usuario : "+id +" no tiene dinero para los carros",HttpStatus.OK);
	}
	
	private ResponseEntity<List<Moto>> fallBackGetMotos(@PathVariable ("usuarioId") Long id,RuntimeException excepcion){
		return new ResponseEntity("El usuario : "+id +" tiene las motos en el taller",HttpStatus.OK);
	}
	
	private ResponseEntity<Moto> fallBackSaveMoto(@PathVariable ("usuarioId") Long id,RuntimeException excepcion,@RequestBody Moto moto){
		return new ResponseEntity("El usuario : "+id +" no tiene dinero para las motos",HttpStatus.OK);
	}
	
	private ResponseEntity<Map<String, Object>> fallBackGetTodos(@PathVariable ("usuarioId") Long id,RuntimeException excepcion){
		return new ResponseEntity("El usuario : "+id +" tiene los vehiculos en el taller",HttpStatus.OK);
	}
	
}
