import { Component, OnInit, Input } from '@angular/core';
import { Cliente } from '../cliente';
import { ClienteService } from '../cliente.service';
import { ModalService } from './modal.service';
import swal from 'sweetalert2';
import { HttpEventType } from '@angular/common/http';

@Component({
  selector: 'detalle-cliente',
  templateUrl: './detalle.component.html',
  styleUrls: ['./detalle.component.css']
})
export class DetalleComponent implements OnInit {

  public titulo: string = 'Detalle del cliente';
  @Input() public cliente: Cliente;
  public imagenSeleccionada: File;
  public progreso: number = 0;

  constructor(private clienteService: ClienteService,
    public modalService: ModalService) { }

  ngOnInit(): void {}

  public seleccionarFoto(event) {
    this.imagenSeleccionada = event.target.files[0]; //De esta forma obtenemos la imágen seleccionada
    this.progreso = 0;

    if (this.imagenSeleccionada.type.indexOf('image') < 0) { //Busca alguna coincidencia con al tipo image
      swal.fire('Error selección de imágen: ', 'Debe seleccionar un tipo de imágen válida', 'error');
      this.imagenSeleccionada = null;
    }
  }

  public subirFoto() {
    if (!this.imagenSeleccionada) {
      swal.fire('Error Upload: ', 'Debe seleccionar una foto', 'error');
    } else {
      this.clienteService.uploadImage(this.imagenSeleccionada, this.cliente.id).subscribe(
        event => {
          if (event.type === HttpEventType.UploadProgress) {
            this.progreso = Math.round((event.loaded/event.total) * 100); //Calculamos la carga de la imágen
          } else if (event.type === HttpEventType.Response) {
            let response: any = event.body;
            this.cliente = response.cliente as Cliente;

            this.modalService.notificarUpload.emit(this.cliente);
            swal.fire('La foto ha sido subida correctamente!', response.mensaje, 'success');
          }
        }
      );
    }

  }

  public cerrarModal() {
    this.modalService.cerrarModal();
    this.imagenSeleccionada = null;
    this.progreso = 0;
  }

}
