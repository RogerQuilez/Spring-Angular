package com.bolsadeideas.springboot.backend.apirest.controllers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bolsadeideas.springboot.backend.apirest.models.entity.Cliente;
import com.bolsadeideas.springboot.backend.apirest.models.entity.Region;
import com.bolsadeideas.springboot.backend.apirest.models.services.IClienteService;
import com.bolsadeideas.springboot.backend.apirest.models.services.IUploadFileService;

@CrossOrigin(origins= {"http://localhost:4200"}) //Con cors damos permiso para el intercambio de datos con otro dominio
@RestController
@RequestMapping("/api")
public class ClienteRestController {
	
	@Autowired
	private IClienteService clienteService;
	
	@Autowired
	private IUploadFileService uploadService;
	
	@GetMapping("/clientes")
	public List<Cliente> index() {
		return clienteService.findAll();
	}
	
	@GetMapping("/clientes/page/{page}")
	public Page<Cliente> indexPage(@PathVariable Integer page) {
		return clienteService.findAll(PageRequest.of(page, 4));
	}
	
	@GetMapping("/clientes/{id}")
	public ResponseEntity<?> show(@PathVariable Long id) { //ResponseEntity nos sirve para comprobar si existe el cliente buscado
		
		Cliente cliente = null;
		Map<String, Object> response = new HashMap<>();
		
		try {
			cliente = clienteService.findById(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar la consulta a la base de datos");
			response.put("error", e.getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		/* En caso de que el cliente sea null devolveremos un error */
		if (cliente == null) {
			response.put("mensaje", "El cliente con ID: " + id.toString() + " no existe en la base de datos!");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<Cliente>(cliente, HttpStatus.OK);
		
	}
	
	@PostMapping("/clientes")
	@ResponseStatus(HttpStatus.CREATED) //Indica que se ha creado contenido en la respuesta
	public ResponseEntity<?> create(@Valid @RequestBody Cliente cliente, BindingResult result) { //RequestBody ya que nos envian los datos en formato JSON
		
		Map<String, Object> response = new HashMap<>();
		Cliente clienteNew = null;
		
		if (result.hasErrors()) {
			/* Metodo anterior para mostrar los errores a JDK 8
			List<String> listErrors = new ArrayList<>();
			for (FieldError err: result.getFieldErrors()) {
				listErrors.add("El campo " + err.getField() + " " + err.getDefaultMessage());
			}*/
			
			//Metodo Actual
			List<String> listErrors = result.getFieldErrors()
				.stream()
				.map(err -> "El campo " + err.getField() + " " + err.getDefaultMessage())
				.collect(Collectors.toList());
			
			response.put("errors", listErrors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		try {
			clienteNew = clienteService.save(cliente);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar la consulta a la base de datos");
			response.put("error", e.getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("mensaje", "El cliente ha sido creado con éxito!");
		response.put("cliente", clienteNew);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
	
	@PutMapping("/clientes/{id}")
	public ResponseEntity<?> update(@Valid @RequestBody Cliente cliente, @PathVariable Long id, BindingResult result) {
		
		Map<String, Object> response = new HashMap<>();
		Cliente updateClient = clienteService.findById(id);
		
		if (result.hasErrors()) {

			List<String> listErrors = result.getFieldErrors()
				.stream()
				.map(err -> "El campo " + err.getField() + " " + err.getDefaultMessage())
				.collect(Collectors.toList());
			
			response.put("errors", listErrors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		if (updateClient == null) {
			response.put("mensaje", "El cliente con ID: " + id.toString() + " no existe en la base de datos!");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		} 
		
		try {
			
			updateClient.setApellido(cliente.getApellido());
			updateClient.setNombre(cliente.getNombre());
			updateClient.setEmail(cliente.getEmail());
			updateClient.setRegion(cliente.getRegion());
			
			clienteService.save(updateClient);
			
		} catch (DataAccessException e) {
			
			response.put("mensaje", "Error al realizar la consulta a la base de datos");
			response.put("error", e.getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		
		response.put("mensaje", "El cliente ha sido actualizado con éxito!");
		response.put("cliente", updateClient);
		
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
	
	/**
	 * Método para eliminar un cliente de la base de datos
	 * @param id
	 * @return
	 */
	@DeleteMapping("/clientes/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public ResponseEntity<?> delete(@PathVariable Long id) {
		
		Map<String, Object> response = new HashMap<>();
		
		try {
			
			Cliente cliente = clienteService.findById(id);
			
			String nombreFotoAnterior = cliente.getFoto();
			
			uploadService.eliminar(nombreFotoAnterior);
			
			clienteService.delete(id);
			
		} catch (DataAccessException e) {
			
			response.put("mensaje", "Error al realizar la consulta a la base de datos");
			response.put("error", e.getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("mensaje", "El cliente ha sido eliminado con éxito!");
		
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
	
	/**
	 * Método para subir la imágen del cliente
	 * @param archivo
	 * @param id
	 * @return
	 */
	@PostMapping("/clientes/upload")
	public ResponseEntity<?> upload(@RequestParam("archivo") MultipartFile archivo, @RequestParam("id") Long id) {
		
		Map<String, Object> response = new HashMap<>();
		
		Cliente cliente = clienteService.findById(id);
		
		if (!archivo.isEmpty()) {
			
			String nombreArchivo;
			
			try {
				
				nombreArchivo = uploadService.copiar(archivo);
				
			} catch (IOException e) {
				response.put("mensaje", "Error al subir la imágen ");
				response.put("error", e.getMessage());
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			
			String nombreFotoAnterior = cliente.getFoto();
			
			uploadService.eliminar(nombreFotoAnterior);
			
			cliente.setFoto(nombreArchivo);
			
			clienteService.save(cliente);
			
			response.put("cliente", cliente);
			response.put("mensaje", "Has subido correctamente la imágen: " + nombreArchivo);
		}
		
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
		
	}
	
	/**
	 * Metodo para mostrar la imágen
	 * @param nombreFoto
	 * @return
	 */
	@GetMapping("/uploads/img/{nombreFoto:.+}")
	public ResponseEntity<Resource> verFoto(@PathVariable String nombreFoto) {
		
		
		Resource recurso = null;
		
		try {
			recurso = uploadService.cargar(nombreFoto);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		HttpHeaders cabecera = new HttpHeaders();
		cabecera.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + recurso.getFilename() + "\"");
		
		return new ResponseEntity<Resource>(recurso, cabecera, HttpStatus.OK);
	}
	
	@GetMapping("/clientes/regiones")
	public List<Region> listarRegiones() {
		return clienteService.findAllRegiones();
	}
	
}
