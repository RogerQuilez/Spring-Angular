import { Component, OnInit, Input, OnChanges, SimpleChanges } from '@angular/core';

@Component({
  selector: 'paginator-nav',
  templateUrl: './paginator.component.html'
})
export class PaginatorComponent implements OnInit, OnChanges {

  @Input() paginador: any; //Permite inyectar un atributo en la clase hijo
  paginas: number[];
  desde: number;
  hasta: number;

  constructor() { }

  ngOnInit(): void {
    this.initPaginator();
  }

  /* Cuando haya un cambio en el componente se ejecutará este metodo */
  ngOnChanges(changes: SimpleChanges): void {
    let paginadorActualizado = changes['paginador'];
    
    if (paginadorActualizado.previousValue) {
      this.initPaginator();
    }
  }

  private initPaginator(): void {
    /*De esta manera hacemos que tenga un máximo de páginas para mostrar en el scroll */
    this.desde = Math.min(Math.max(1, this.paginador.number - 4), this.paginador.totalPages - 5);
    this.hasta = Math.max(Math.min(this.paginador.totalPages, this.paginador.number + 4), 6);

    if (this.paginador.totalPages > 5) {
      this.paginas = new Array(this.hasta - this.desde + 1).fill(0).map((_valor, indice) => indice + this.desde); //Lista iterable de páginas
    } else {
      this.paginas = new Array(this.paginador.totalPages).fill(0).map((_valor, indice) => indice + 1); //Lista iterable de páginas
    }
  }

}
