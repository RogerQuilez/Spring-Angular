import { Injectable, EventEmitter } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ModalService {

  public modal: boolean = false;

  private _notificarUpload = new EventEmitter<any>(); //atributo para no tener que recargar la pagina para ver el cambio de imagen

  constructor() { }

  get notificarUpload(): EventEmitter<any> {
    return this._notificarUpload;
  }

  public abrirModal() {
    this.modal = true;
  }

  public cerrarModal() {
    this.modal = false;
  }
}
