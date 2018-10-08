Vue.component('contacters-row',{
    props: ['contacter'],
    template: '<div><i>({{ contacter.id }})</i>contacter.text</div>'

});
// Определяем новый компонент под именем todo-item
Vue.component('contacters-list', {
    props: ['contacters'],
    template:
    '<div><contacters-row v-for="contacter in contacters" :key = "contacter.id ":contacter = "contacter"/></div>'
});
var app = new Vue({
    el: '#app',
    template: '<contacters-list :contacters="contacters"/>',
    data: {
        contacters: [
            {id: '1', text: 'Wow0'},
            {id: '2', text: 'Wow1'},
            {id: '3', text: 'Wow2'},
            {id: '4', text: 'Wow3'}
        ]
    }
});
