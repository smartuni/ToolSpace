/*var AppComponent = ng.core.Component({
    selector : 'hello',
    template : '<p>The ID is {{greeting.id}}</p><p>The content is {{greeting.content}}</p>'
}).Class({
    constructor : [ng.http.Http, function(http) {
        var self = this;
        self.greeting = {id:'', content:''};
        http.get("/resource").subscribe(response => self.greeting = response.json());
    }]
});*/

var routes = [
    { path: '', pathMatch: 'full', redirectTo: 'home'},
    { path: 'home', component: HomeComponent},
    { path: 'login', component: LoginComponent}
];

var AppComponent = ng.core.Component({
    templateUrl: 'app.html',
    selector: 'app',
    providers: [AppService]
}).Class({constructor : [AppService, ng.http.Http, ng.router.Router, function(app, http, router){

    this.logout = function() {
        http.post('logout', {}).finally(function() {
            app.authenticated = false;
            router.navigateByUrl('/login')
        }).subscribe();
    }

    app.authenticate();
}]
});

var AppModule = ng.core.NgModule({
    imports: [ng.platformBrowser.BrowserModule, ng.http.HttpModule,
        ng.router.RouterModule.forRoot(routes), ng.forms.FormsModule],
    declarations: [HomeComponent, LoginComponent, AppComponent],
    bootstrap: [AppComponent]
}).Class({constructor : function(){}});

document.addEventListener('DOMContentLoaded', function() {
    ng.platformBrowserDynamic.platformBrowserDynamic().bootstrapModule(AppModule);
});

var AppService = ng.core.Injectable({}).Class({constructor: [ng.http.Http, function(http) {

    var self = this;
    this.authenticated = false;
    this.authenticate = function(credentials, callback) {

        var headers = credentials ? {
            authorization : "Basic " + btoa(credentials.username + ":" + credentials.password)
        } : {};
        http.get('user', {headers: headers}).subscribe(function(response) {
            if (response.json().name) {
                self.authenticated = true;
            } else {
                self.authenticated = false;
            }
            callback && callback();
        });

    }

}]})