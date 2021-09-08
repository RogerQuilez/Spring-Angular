package com.bolsadeideas.springboot.backend.apirest.models.services.impl;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bolsadeideas.springboot.backend.apirest.models.services.IUploadFileService;

@Service
public class UploadFileServiceImpl implements IUploadFileService {

	private final Logger log = LoggerFactory.getLogger(UploadFileServiceImpl.class);

	private final static String DIRECTORIO_UPLOAD = "uploads";

	@Override
	public Resource cargar(String nombreArchivo) throws MalformedURLException {

		Path rutaArchivo = this.getPath(nombreArchivo);

		Resource recurso = null;

		recurso = new UrlResource(rutaArchivo.toUri());

		if (!recurso.exists() && !recurso.isReadable()) {

			// En caso de que se borre la imagen en el backend se controlara y se
			// actualizara a la imagen por defecto
			rutaArchivo = Paths.get("src/main/resources/static/images").resolve("no-image.png").toAbsolutePath();

			recurso = new UrlResource(rutaArchivo.toUri());

			log.error("No se pudo cargar la imágen");
		}

		return recurso;
	}

	@Override
	public String copiar(MultipartFile archivo) throws IOException {
		
		String nombreArchivo = /* Genera un Identificador random */ UUID.randomUUID().toString() + "_"
				+ archivo.getOriginalFilename().replace(" ", "");

		Path rutaArvhico = this.getPath(nombreArchivo);

		log.info(rutaArvhico.toString());

		Files.copy(archivo.getInputStream(), rutaArvhico);
		
		return nombreArchivo;
		
	}

	@Override
	public boolean eliminar(String nombreArchivo) {
		
		if (nombreArchivo != null && nombreArchivo.length() > 0) {
			Path rutaFotoAnterior = Paths.get("uploads").resolve(nombreArchivo).toAbsolutePath();
			File archivoFotoAnterior = rutaFotoAnterior.toFile();
			
			if (archivoFotoAnterior.exists() && archivoFotoAnterior.canRead()) {
				archivoFotoAnterior.delete();
				return true;
			}
		}
		
		return false;
	}

	@Override
	public Path getPath(String nombreArchivo) {
		return Paths.get(DIRECTORIO_UPLOAD).resolve(nombreArchivo).toAbsolutePath();
	}

}
