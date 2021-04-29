import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {SupportService} from "../../service/support.service";
import {FormControl} from "@angular/forms";

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.scss']
})
export class ChatComponent implements OnInit {
  answerForm = new FormControl();
  id:string;
  authorInfo:any;
  supportAnswer:any;
  fileListToDownload: [any];
  status: string;
  subject: string;
  theme: string;
  text: string;
  constructor(private router: Router, private activatedRoute: ActivatedRoute,private supportService: SupportService) { }

  ngOnInit(): void {
    this.activatedRoute.queryParamMap.subscribe((param) => {
      this.id = param.get("id");
    });
    this.supportService.getSupportListItem(this.id).then(resp=>{
      this.authorInfo = resp.data.author;
      this.fileListToDownload = resp.data.fileList;
      this.status = resp.data.status;
      this.subject = resp.data.subject;
      this.theme = resp.data.theme;
      this.text = resp.data.text;
      this.supportAnswer = resp.data.supportAnswer;
    })
  }
  navigateToHelp(){
    this.router.navigate(['/admin-panel/help'])
  }
  // author: {id: 12, username: "+38095384343", password: null, realname: null, email: "arsenguzhva@gmail.com",…}
  // active: true
  // blocked: false
  // email: "arsenguzhva@gmail.com"
  // id: 12
  // password: null
  // realname: null
  // roles: [{id: 10, authority: "ROLE_ADMIN"}, {id: 11, authority: "ROLE_USER"}]
  // username: "+38095384343"
  // fileList: [{id: 46, authorId: 12, filename: "00003_0.jpg", type: "image/jpeg",…}]
  // 0: {id: 46, authorId: 12, filename: "00003_0.jpg", type: "image/jpeg",…}
  // authorId: 12
  // extension: ".jpg"
  // filename: "00003_0.jpg"
  // hash: "92078990fc3fc87d67f034e5d4fd8d3223c08894b34e163bafe48c73b5bee027"
  // id: 46
  // path: "92078990fc3fc87d67f034e5d4fd8d3223c08894b34e163bafe48c73b5bee027.jpg"
  // size: 70539
  // type: "image/jpeg"
  // id: 45
  // status: "closed"
  // subject: "Некорректная подпись"
  // supportAnswer: [{operatorId: 12, text: "попробуйте перезагрузить ваш сервер",…}]
  // 0: {operatorId: 12, text: "попробуйте перезагрузить ваш сервер",…}
  // fileList: [{id: 49, authorId: 12, filename: "76ac77be-693b-4317-8780-868bba68c6f7.jpg", type: "image/jpeg",…}]
  // 0: {id: 49, authorId: 12, filename: "76ac77be-693b-4317-8780-868bba68c6f7.jpg", type: "image/jpeg",…}
  // id: 50
  // operatorId: 12
  // text: "попробуйте перезагрузить ваш сервер"
  // text: "Запросы не уходят. Пишет что некорректная подпись"
  // theme: "Проблемы с интеграцией"
  sendAnswer() {
    let answer = {
      "requestId": this.id,
      "text": this.answerForm.value,
      "fileList": []
    };
    this.supportService.answer(answer).then(resp=>{
      this.navigateToHelp()
    })
  }
}
