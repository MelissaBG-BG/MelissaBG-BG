package com.atos.springBoot.backend.apirest.controllers;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.atos.springBoot.backend.apirest.models.entity.Cliente;
import com.atos.springBoot.backend.apirest.models.services.IClienteService;

@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping("/api")
public class ClienteRestController {
	
	@Autowired
	private IClienteService clienteService;
	
	//listar 
	@GetMapping("/clientes")
	public List<Cliente> index(){
		return clienteService.findAll();		
	}

	//mostrar por id
	@GetMapping("/clientes/{id}")
	public ResponseEntity<?> show(@PathVariable Long id) { //retorma el cliente convertido en json
		Cliente cliente = null;
		Map<String, Object> response = new HashMap<>();
		try {
			cliente =clienteService.findById(id);  //utilizando el service retornamos el cliente. Esto seria la implementación	
		}catch(DataAccessException d){
			response.put("mensaje", "Error al realizar la consulta en la base de datos");
			response.put("Error", d.getMessage().concat(": ").concat(d.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}		
		
		if (cliente == null) {
			response.put("mensaje", "El cliente ID: ".concat(id.toString().concat(" no existe en la base de datos!")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Cliente>(cliente, HttpStatus.OK);
	}
	
	
	//El crear nuevo registro seria con el verbo post
	@PostMapping("/clientes")  
	@ResponseStatus(HttpStatus.CREATED)  //como codigo 201 se ha creado contenido. si no se asigna por defecto sera 200
	public ResponseEntity<?> create(@Valid @RequestBody Cliente cliente, BindingResult result) { //como viene en formato json dentro del cuerpo tenemos que indicar que es RequestBody
		
		Cliente clienteNew = null;
		Map<String, Object> response = new HashMap<>();
		
		
		if (result.hasErrors()) {
					
			List<String> errors = result.getFieldErrors()
					.stream()
					.map(err -> "El campo '" +  err.getField() + "' " + err.getDefaultMessage()).collect(Collectors.toList());
			
			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		try {
			clienteNew = clienteService.save(cliente);
		}catch(DataAccessException d) {
			response.put("mensaje", "Error al realizar el insert en la base de datos");
			response.put("Error", d.getMessage().concat(": ").concat(d.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		
		}
		response.put("mensaje", "el cliente  ha sido creado con éxito!");
		response.put("cliente", clienteNew);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);  //retornará el objeto creado cliente.
	}
	
	
	//actualizar cliente
	@PutMapping("/clientes/{id}")  //editar un registro existente
	public ResponseEntity<?> update(@Valid @RequestBody Cliente cliente, BindingResult result, @PathVariable Long id ) {
		
		Cliente clienteActual = clienteService.findById(id);
		Cliente clienteUpdate = null;
		
		Map<String, Object> response = new HashMap<>();
		
		if (result.hasErrors()) {
			
			List<String> errors = result.getFieldErrors()
					.stream()
					.map(err -> "El campo '" +  err.getField() + "' " + err.getDefaultMessage()).collect(Collectors.toList());
			
			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		if (clienteActual == null) {
			response.put("mensaje", "Error:, no se pudo editar, el cliente ID: ".concat(id.toString().concat(" no existe en la base de datos!")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		
		try {
		clienteActual.setNombre(cliente.getNombre());
		clienteActual.setApellido(cliente.getApellido());
		clienteActual.setEmail(cliente.getEmail());
		clienteActual.setCreateAt(cliente.getCreateAt());
			
		clienteUpdate = clienteService.save(clienteActual);
		
		}catch(DataAccessException d) {
			response.put("mensaje", "Error al actualizar en la base de datos");
			response.put("Error", d.getMessage().concat(": ").concat(d.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "el cliente  ha sido actualizado con éxito!");
		response.put("cliente", clienteUpdate);				
		return  new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);  //retornará el objeto creado cliente.
		
	}
	@DeleteMapping("/clientes/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		
		Map<String, Object> response = new HashMap<>();
		
		try {
			clienteService.delete(id);
		}catch(DataAccessException e) {
			response.put("mensaje", "Error al eliminar de la base de datos");
			response.put("Error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "Cliente eliminado con éxito!");
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
			
	}
}

/*esta seria la forma de de manejar los error atraves de la api list(de un arraylist, iterando con un for y guardando los mensajes)*/
/*List<String> errors = new ArrayList<>();

for(FieldError err: result.getFieldErrors()) {
	errors.add("El campo '" +  err.getField() + "' " + err.getDefaultMessage());
} si utilizamos java 8 o superior*/
