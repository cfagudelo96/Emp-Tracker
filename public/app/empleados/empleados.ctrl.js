(function() {
    angular.module('app').controller('EmpleadosCtrl', function($scope, $http, $mdDialog) {

        $scope.opciones = ['Nombre','Cedula'];

        $scope.opcionesAsistencias = ['Descripcion','Fechas','Facilitador','Area','Lugar'];

        $scope.busqueda = {"activo":false};

        $scope.busquedaAsistencia = {};

        $scope.busquedaFecha = {};

        $scope.activos = false;

        $scope.agregar = false;

        $scope.editar = false;

        $scope.empleado = {};

        $scope.empleados = [];

        $http.get('/api/empleados').then(function (response) {
            $scope.empleados = response.data;
        });

        $scope.mostrarTodos = function () {
            $scope.filtro = null;
            $scope.busqueda = {};
            $http.get('/api/empleados').then(function (response) {
                $scope.empleados = response.data;
            }).catch(function (error) {
                $scope.showAlert(error.data);
            });
        };

        $scope.mostrarSinAsistencias = function () {
            $http.get('/api/empleados/sinCapacitaciones').then(function (response) {
                $scope.filtro = 'Filtrando por empleados sin asistencias';
                $scope.empleados = response.data;
            }).catch(function () {
                $scope.showAlert('No se pudo obtener los empleados sin asistencias');
            });
        };

        $scope.mostrarSinAsistenciasFecha = function (evento) {
            $mdDialog.show({
                controller: DialogoFechasController,
                templateUrl: 'public/app/empleados/dialogoFechas.tpl.html',
                parent: angular.element(document.body),
                targetEvent: evento,
                clickOutsideToClose:true,
                fullscreen: $scope.customFullscreen
            }).then(function(busquedaFecha) {
                $http.post('/api/empleados/sinCapacitacionesFecha',busquedaFecha).then(function (response) {
                    $scope.filtro = 'Filtrando por empleados sin asistencias entre fechas';
                    $scope.empleados = response.data;
                }).catch(function () {
                    $scope.showAlert('No se pudo obtener los empleados sin asistencias entre las fechas');
                })
            });
        };

        $scope.irAgregar = function () {
            if(!$scope.editar) {
                $scope.agregar = true;
            }
        };

        $scope.salirAgregar = function () {
            $scope.empleado = {};
            $scope.agregar = false;
        };

        $scope.guardarEmpleado = function () {
            $scope.empleado.activo=true;
            $http.post('/api/empleados',$scope.empleado).then(function (response) {
                $scope.empleados.push(response.data);
                $scope.empleado = {};
                $scope.agregar = false;
            }).catch(function (error) {
                if(error.data.mensaje.includes('UQ_EMPLEADO_CEDULA_INDEX')) {
                    $scope.showAlert('Ya existe un empleado con el número de cédula indicado');
                } else {
                    $scope.showAlert(error.data.mensaje);
                }
            });
        };

        $scope.irEditar = function (index) {
            if(!$scope.agregar) {
                $scope.editar = true;
                $scope.empleado = JSON.parse(JSON.stringify($scope.empleados[index]));
                $scope.empleado.index = index
            }
        }
        
        $scope.salirEditar = function () {
            $scope.empleado = {};
            $scope.editar = false;
        }

        $scope.editarEmpleado = function () {
            $http.put('/api/empleados/'+$scope.empleado.id,$scope.empleado).then(function (response) {
                $scope.empleados[$scope.empleado.index] = response.data;
                $scope.empleado = {};
                $scope.editar = false;
            }).catch(function (error) {
                if(error.data.mensaje.includes('UQ_EMPLEADO_CEDULA_INDEX')) {
                    $scope.showAlert('Ya existe un empleado con el número de cédula indicado');
                } else {
                    $scope.showAlert(error.data.mensaje);
                }
            });
        };

        $scope.eliminarEmpleado = function (evento, index) {
            var confirm = $mdDialog.confirm()
                .title('¿Desea eliminar el empleado?')
                .textContent('Se eliminará el registro del empleado y todas sus asistencias.')
                .ariaLabel('Eliminar empleado')
                .targetEvent(evento)
                .ok('Eliminar')
                .cancel('Cancelar');
            $mdDialog.show(confirm).then(function() {
                $http.delete('/api/empleados/'+$scope.empleados[index].id).then(function (response) {
                    $scope.empleados.splice(index,1);
                }).catch(function (error) {
                    $scope.showAlert('Ocurrió un error al eliminar el empleado');
                });
            });
        };

        $scope.showAlert = function(error) {
            $mdDialog.show(
                $mdDialog.alert()
                    .clickOutsideToClose(true)
                    .title('Error')
                    .textContent(error)
                    .ariaLabel('Error')
                    .ok('Ok')
            );
        };

        $scope.buscarEmpleados = function () {
            $http.post('/api/empleados/buscar',$scope.busqueda).then(function (response) {
                $scope.filtro = "Filtrando empleados por "+$scope.busqueda.atributo+" con valor "+$scope.busqueda.valor;
                $scope.empleados = response.data;
            }).catch(function (error) {
                $scope.showAlert(error.data.mensaje);
            });
        };

        $scope.verAsistencias = function (index) {
            $http.get('/api/empleados/'+$scope.empleados[index].id+'/asistencias').then(function (response) {
                $scope.nombreEmpleado = $scope.empleados[index].nombre
                $scope.idEmpleado = $scope.empleados[index].id
                $scope.mostrarAsistencias = true;
                $scope.asistencias = response.data;
            }).catch(function () {
                $scope.showAlert('No se pudo obtener las asistencias del empleado');
            });
        };

        $scope.ocultarAsistencias = function () {
            $scope.mostrarAsistencias = false;
            $scope.busquedaAsistencia = {};
            $scope.busquedaFecha = {};
        };

        $scope.filtrarAsistencias = function () {
            if($scope.busquedaAsistencia.atributo===$scope.opcionesAsistencias[1]) {
                $http.post('/api/empleados/'+$scope.idEmpleado+'/asistencias/fechas',$scope.busquedaFecha).then(function (response) {
                    $scope.asistencias = response.data;
                }).catch(function () {
                    $scope.showAlert('No se pudo filtrar las asistencias del empleado');
                });
            } else {
                $http.post('/api/empleados/'+$scope.idEmpleado+'/asistencias',$scope.busquedaAsistencia).then(function (response) {
                    $scope.asistencias = response.data;
                }).catch(function () {
                    $scope.showAlert('No se pudo filtrar las asistencias del empleado');
                });
            }
        };

        $scope.mostrarTodasAsistencias = function () {
            $http.get('/api/empleados/'+$scope.idEmpleado+'/asistencias').then(function (response) {
                $scope.asistencias = response.data;
            }).catch(function () {
                $scope.showAlert('No se pudo obtener las asistencias del empleado');
            });
        };

        $scope.mostrarDetalles = function (i) {
            var observaciones = ($scope.asistencias[i].capacitacion.observaciones ? $scope.asistencias[i].capacitacion.observaciones : "Sin observaciones");
            var texto = "<b>Descripcion: </b>"+$scope.asistencias[i].capacitacion.descripcion+"<br>" +
                "<b>Fecha de inicio:</b> "+$scope.millisToFecha($scope.asistencias[i].capacitacion.fechaInicio)+"<br>" +
                "<b>Fecha de finalizacion:</b> "+$scope.millisToFecha($scope.asistencias[i].capacitacion.fechaFinal)+"<br>" +
                "<b>Area:</b> "+$scope.asistencias[i].capacitacion.area+"<br>" +
                "<b>Lugar:</b> "+$scope.asistencias[i].capacitacion.lugar+"<br>" +
                "<b>Facilitador:</b> "+$scope.asistencias[i].capacitacion.facilitador+"<br>" +
                "<b>Costo:</b> $"+$scope.asistencias[i].capacitacion.costo+"<br>" +
                "<b>Observaciones:</b> "+observaciones;
            $mdDialog.show(
                $mdDialog.alert()
                    .clickOutsideToClose(true)
                    .title('Detalles')
                    .htmlContent(texto)
                    .ariaLabel('Detalles')
                    .ok('Ok')
            );
        };

        $scope.millisToFecha = function (millis) {
            var date = new Date(millis);
            return date.getDate()+"/"+(date.getMonth()+1)+"/"+date.getFullYear();
        };

        function DialogoFechasController($scope, $mdDialog) {
            $scope.busquedaFecha = {};

            $scope.hide = function() {
                $mdDialog.hide();
            };

            $scope.cancel = function() {
                $mdDialog.cancel();
            };

            $scope.answer = function() {
                $mdDialog.hide($scope.busquedaFecha);
            };
        };
    });
})();