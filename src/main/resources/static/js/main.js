function getIndex(list, id) {
    for (var i = 0; i < list.length; i++ ) {
        if (list[i].id === id) {
            return i;
        }
    }

    return -1;
}
var messageApi = Vue.resource('/message{/id}');

Vue.component('message-form',{
    props:['messages','messageFromRow'],
    data: function() {
        return {
            text: '',
            id:''
        }
    },
    watch:{
        messageFromRow:function(newVal,oldVal){
            this.text = newVal.text;
            this.id = newVal.id;
        }
    },
   template:
       '<div style="position: relative; width: 300px;">' +
       '<input type="text" placeholder="write something" v-model="text"/>'+
       '<input type="button" value="save" v-on:click="save"/>'+
       '</div>',
    methods:{
       save:function (){
              var message = {text:this.text} ;
              if(this.id){
                  messageApi.update({id:this.id},message).then(result=>result.json().then(answer=>{
                      var tmp =getIndex(this.messages, answer.id);
                      this.messages.splice(tmp,1,answer);
                  }));
              }
              else {
                  messageApi.save({}, message)
                      .then(result => result.json()
                          .then(answer => {
                              console.log(answer);
                              this.messages.push(answer);
                              this.text = '';
                          }));
              }
            }
    }
});
Vue.component('message-row',{
    props:['current_message','editMsg','allMessages'],
   template:'<div>' +
       '<i>( {{current_message.id}} )</i> {{ current_message.text}}' +
       '<span style="position: absolute; right: 0">'+
       '<input type="button" value="edit" @click="edit">'+
       '<input type="button" value="X" @click="del">'+
       '</span>'+
       '</div>',
    methods:{
        edit:function () {
            this.editMsg(this.current_message);
        },
        del:function () {
            messageApi.remove({id:this.current_message.id}).then(result=>{
                if(result.ok){
                    this.allMessages.splice(this.allMessages.indexOf(this.current_message),1);
                }
            });
        }
    }
});
Vue.component('messages-list', {
    props:['listOfMessages'],
    data:function() {
        return {
            message:null
        }
    },
    template: '<div>' +
        '<message-form :messages="listOfMessages" :messageFromRow="message"/>'+
        '<message-row v-for="message in listOfMessages" :key = "message.id":current_message="message" :editMsg ="editMessage" :allMessages="listOfMessages"/>' +
        '</div>',
    created:function () {
          messageApi.get()
              .then(result=>result.json()
                  .then(
                    data=>{
                        console.log(data);
                        data.forEach(message =>this.listOfMessages.push(message));
                    }
              ));
    },
    methods:{
        editMessage:function (message) {
            this.message = message;
        }
    }
});
var app = new Vue({
    el: '#app',
    template:'<messages-list :listOfMessages = "messages" />',
    data: {
        messages:[]
        

    }
})