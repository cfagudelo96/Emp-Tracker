(function() {
    angular.module('app', ['ngRoute', 'ngMaterial', 'ngMessages', 'ngSanitize']).config(function($routeProvider) {
        var rootDir = '/public/app/';
        $routeProvider
            .when('/', {
                controller: 'HomeCtrl',
                templateUrl: rootDir+'home/home.html'
            }).when('/empleados', {
                controller: 'EmpleadosCtrl',
                templateUrl: rootDir+'empleados/empleados.tpl.html'
            }).when('/capacitaciones', {
                controller: 'CapacitacionesCtrl',
                templateUrl: rootDir+'capacitaciones/capacitaciones.tpl.html'
            });
    });
})();