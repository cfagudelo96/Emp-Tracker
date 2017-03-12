(function() {
    angular.module('app').controller('AsistenciasCtrl', function($scope, $http, $mdDialog, capacitacion) {
        $scope.empleados = [];

        $scope.empleadosSeleccionados = [];

        $scope.asistencia = {};

        $http.get('/api/empleados/activos').then(function (response) {
            $scope.empleados = response.data;
        });

        $scope.seleccionarEmpleado = function(empleado) {
            var index = $scope.empleados.indexOf(empleado);
            if(!$scope.empleados[index].seleccionado) {
                $scope.empleados[index].seleccionado = true;
                $scope.empleadosSeleccionados.push($scope.empleados[index]);
            } else {
                var i = $scope.empleadosSeleccionados.indexOf($scope.empleados[index]);
                $scope.empleados[index].seleccionado = false;
                $scope.empleadosSeleccionados.splice(i,1);
            }
        };

        $scope.agregarAsistencias = function () {
            $scope.asistencia.empleados = $scope.empleadosSeleccionados;
            $scope.asistencia.capacitacion = capacitacion;
            $http.post('/api/asistencias',$scope.asistencia).then(function () {
                $mdDialog.hide("Asistencias registradas exitósamente")
            }).catch(function (result) {
                console.log(result);
                $mdDialog.hide("Ocurrió un error al registrar las asistencias")
            });
        };

        $scope.cerrar = function () {
            $mdDialog.cancel();
        };
    });
})();
