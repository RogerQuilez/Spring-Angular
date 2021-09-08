import { Component, OnInit } from '@angular/core';
import { Cliente } from './cliente';
import { Region } from './region';
import { ClienteService } from './cliente.service';
import { Router, ActivatedRoute } from '@angular/router';
import swal from 'sweetalert2';

@Component({
  selector: 'app-form',
  templateUrl: './form.component.html'
})
export class FormComponent implements OnInit {

  public cliente: Cliente = new Cliente();
  public regiones: Region[];
  public titulo: string = "Crear Cliente";
  public errors: string[] = null;

  constructor(private clienteService: ClienteService,
  private router: Router,
  private activatedRoute: ActivatedRoute) { }

  ngOnInit(): void {
    this.cargarCliente(); //Lo llamamos cuando se inicializa el componente
  }

  //Comprobar si existe el cliente
  public cargarCliente(): void {
    this.activatedRoute.params.subscribe(params => {
      let id = params['id']
      if (id) {
        this.clienteService.getCliente(id).subscribe( (cliente) => this.cliente = cliente);
      }
    });

    this.clienteService.getRegiones().subscribe(regiones => this.regiones = regiones);
  }

  /* Creación de un nuevo cliente */
  public create(): void {
    this.clienteService.create(this.cliente).subscribe(
      response => {
        this.router.navigate(['/clientes']) //Redirige a la ruta de clientes al guardar el cliente
        swal.fire('Nuevo Cliente', `Cliente ${response.cliente.nombre} creado con éxtio!`, 'success');
      },
      err => {
        this.errors = err.error.errors as string[];
      }
    );
  }

  //Modificar un cliente
  public update(): void {
    this.clienteService.update(this.cliente).subscribe(
      response => {
        this.router.navigate(['/clientes'])
        swal.fire('Cliente actualizado', `El cliente ${response.cliente.nombre} se ha actualizado correctamente`, 'success');
      },
      err => {
        this.errors = err.error.errors as string[];
      }
    )
  }

  public compararRegion(obj1: Region, obj2: Region) {
    if (obj1 == undefined && obj2 == undefined) return true;
    return obj1 == null || obj2 == null ? false: obj1.id === obj2.id;
  }

}
