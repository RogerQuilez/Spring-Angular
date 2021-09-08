import { Injectable } from '@angular/core';
import { formatDate } from '@angular/common';
import { Cliente } from './cliente';
import { Region } from './region';
import { of, Observable, throwError } from 'rxjs';
import { map, catchError, tap } from 'rxjs/operators';
import { HttpClient, HttpHeaders, HttpRequest, HttpEvent } from '@angular/common/http';
import swal from 'sweetalert2';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class ClienteService {

  private urlEndPoint: string = 'http://localhost:8081/api/clientes'; //Definimos el End point
  private httpHeaders = new HttpHeaders({'Content-type': 'application/json'})

  constructor(private http: HttpClient,
    private router: Router) { }

  public getRegiones(): Observable<Region[]> {
    return this.http.get<Region[]>(this.urlEndPoint + '/regiones');
  }

  public getClientes(page: number): Observable<any> {
    //return of(CLIENTES); //Convertimos nuestro flujo Observable a partir de los objetos CLIENTES
    //return this.http.get<Cliente[]>(this.urlEndPoint); //De esta manera usamos el mapeo que devuelve Spring Boot con el JSON clientes para convertirlo y poder consumirlo
    return this.http.get(this.urlEndPoint + '/page/' + page).pipe(
      tap( (response: any) => {
        (response.content as Cliente[]).forEach( cliente => {
          console.log(cliente.nombre);
        })
      }),
      map( (response: any) => { //Map se encarga de hacer la conversión del JSON (response) a clientes
        (response.content as Cliente[]).map(cliente => {
          cliente.nombre = cliente.nombre.toUpperCase(); //Cambiamos a mayusculas el nombre de los clientes
          //cliente.createAt = formatDate(cliente.createAt, 'EEE dd, MMMM yyyy', 'es');
          return cliente;
        });
        return response;
      }),
      tap(response => {
        console.log('ClienteService: tap 2');
        (response.content as Cliente[]).forEach( cliente => {
          console.log(cliente.nombre);
        })
      }),
    )
  }

  public create(cliente: Cliente): Observable<any> {
    return this.http.post<any>(this.urlEndPoint, cliente, {headers: this.httpHeaders}).pipe(
      catchError(e => { //En caso de que haya un error, devolverá el error que hemos enviado desde Spring Boot en formato JSON

        if (e.status == 4) {
          return throwError(e);
        }

        swal.fire('Error al crear', e.error.error, 'error');
        return throwError(e);
      })
    );
  }

  public getCliente(id): Observable<Cliente> {
    return this.http.get<Cliente>(`${this.urlEndPoint}/${id}`).pipe(
      catchError(e => { //En caso de que haya un error, devolverá el error que hemos enviado desde Spring Boot en formato JSON
        this.router.navigate(['/clientes']);
        swal.fire('Error al buscar', e.error.mensaje, 'error');
        return throwError(e);
      })
    );
  }

  public update(cliente: Cliente): Observable<any> {
    return this.http.put<any>(`${this.urlEndPoint}/${cliente.id}`, cliente, {headers: this.httpHeaders}).pipe(
      catchError(e => { //En caso de que haya un error, devolverá el error que hemos enviado desde Spring Boot en formato JSON

        if (e.status == 4) {
          return throwError(e);
        }

        this.router.navigate(['/clientes']);
        swal.fire('Error al editar', e.error.error, 'error');
        return throwError(e);
      })
    );
  }

  public delete(id): Observable<Cliente> {
    return this.http.delete<Cliente>(`${this.urlEndPoint}/${id}`, {headers: this.httpHeaders}).pipe(
      catchError(e => { //En caso de que haya un error, devolverá el error que hemos enviado desde Spring Boot en formato JSON
        this.router.navigate(['/clientes']);
        swal.fire('Error al eliminar', e.error.error, 'error');
        return throwError(e);
      })
    );
  }

  public uploadImage(archivo: File, id): Observable<HttpEvent<{}>> {

    let formData = new FormData();
    formData.append("archivo", archivo);
    formData.append("id", id);

    const req = new HttpRequest('POST', `${this.urlEndPoint}/upload`, formData, {
      reportProgress: true
    });

    return this.http.request(req);

  }
}
