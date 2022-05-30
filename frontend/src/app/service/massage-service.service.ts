import { Injectable } from '@angular/core';
import {Subject} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class MassageServiceService {

  constructor() { }

  // Observable string sources
  private massageAnnouncedSource = new Subject<string>();
  private newMassagesSource = new Subject<string>();

  // Observable string streams
  missionAnnounced$ = this.massageAnnouncedSource.asObservable();
  newMassagesSubscription$ = this.newMassagesSource.asObservable();

  // Service message commands
  announceMassage(massage: string) {
    console.log(massage);
    this.massageAnnouncedSource.next(massage);
  }

  confirmMassage(astronaut: string) {
    this.newMassagesSource.next(astronaut);
  }

  showMassage(massageType: string) {
    // this.massageModalComponent.openWithMassageType(massageType);
  }
}
