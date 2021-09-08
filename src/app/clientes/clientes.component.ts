import { Component, OnInit } from '@angular/core';
import { Cliente } from './cliente';
import { ClienteService } from './cliente.service';
import { ModalService } from './detalle/modal.service';
import swal from 'sweetalert2';
import { tap } from 'rxjs/operators';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-clientes',
  templateUrl: './clientes.component.html'
})
export class ClientesComponent implements OnInit {

  public clientes: Cliente[];
  public paginador: any;
  public clienteSeleccionado: Cliente;

  constructor(private clienteService: ClienteService,
    private modalService: ModalService,
    private activatedRoute: ActivatedRoute) { } //De esta manera generamos un atributo de la clase automaticamente en el constructor

  ngOnInit(): void { //Este metodo es llamado cuando se inicia el componente

  this.activatedRoute.paramMap.subscribe(params => { //paramMap se encarga de observar
    let page: number = +params.get('page'); //Con + convertimos el string a integer

    if (!page) page = 0;

    this.clienteService.getClientes(page).pipe(
        tap(response => {
          console.log('ClienteService: tap 3');
          (response.content as Cliente[]).forEach(cliente => {
            console.log(cliente.nombre);
          });
        })
      ).subscribe(response => {
        this.clientes = response.content as Cliente[]; //Función que recibe Clientes (resultado del stream) y lo asigna al atributo clientes de la clase)
        this.paginador = response; //Response será nuestro paginador
      });
    });

    this.modalService.notificarUpload.subscribe(cliente => {
      this.clientes = this.clientes.map(clienteOriginal => {
        if (cliente.id == clienteOriginal.id) {
          clienteOriginal.foto = cliente.foto;
        }
        return clienteOriginal;
      })
    })
  }

  delete(cliente: Cliente): void {
    swal.fire({
      title: 'Está seguro?',
      text: `Seguro que desea eliminar al cliente ${cliente.nombre}?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Si, eliminar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.clienteService.delete(cliente.id).subscribe(
          response => {
            this.clientes = this.clientes.filter(cli => cli != cliente) //Esta linea hace que se actualice la lista de clientes en la vista
            swal.fire(
              'Cliente Eliminado!',
              `El cliente ${cliente.nombre} se ha eliminado correctamente`,
              'success'
            )
          }
        )

      }
    })
  }

  abrirModal(cliente: Cliente) {
    this.clienteSeleccionado = cliente;
    this.modalService.abrirModal();
  }

}
