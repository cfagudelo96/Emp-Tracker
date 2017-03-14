(function () {
   angular.module('app').controller('CapacitacionesCtrl',function ($scope,$http,$mdDialog,$mdToast) {

       $scope.opciones = ['Descripcion','Fechas','Facilitador','Area','Lugar'];

       $scope.opcionesAsistencias = ['Nombre','Cedula'];

       $scope.busquedaAsistencia = {};

       $scope.busqueda = {};

       $scope.busquedaFecha = {};

       $scope.capacitaciones = [];

       $scope.capacitacion = {};

       $scope.formData = null;

       $scope.agregar = false;

       $scope.editar = false;

       $http.get('/api/capacitaciones').then(
           function (response) {
               $scope.capacitaciones=response.data;
           }
       );

       $scope.mostrarTodos = function () {
           $scope.filtro = null;
           $scope.busqueda = {};
           $http.get('/api/capacitaciones').then(
               function (response) {
                   $scope.capacitaciones=response.data;
               }
           );
       };

       $scope.mostrarDetalles = function (i) {
           var observaciones = ($scope.capacitaciones[i].observaciones ? $scope.capacitaciones[i].observaciones : "Sin observaciones");
           var texto = "<b>Descripcion: </b>"+$scope.capacitaciones[i].descripcion+"<br>" +
               "<b>Fecha de inicio:</b> "+$scope.millisToFecha($scope.capacitaciones[i].fechaInicio)+"<br>" +
               "<b>Fecha de finalizacion:</b> "+$scope.millisToFecha($scope.capacitaciones[i].fechaFinal)+"<br>" +
               "<b>Area:</b> "+$scope.capacitaciones[i].area+"<br>" +
               "<b>Lugar:</b> "+$scope.capacitaciones[i].lugar+"<br>" +
               "<b>Facilitador:</b> "+$scope.capacitaciones[i].facilitador+"<br>" +
               "<b>Costo:</b> $"+$scope.capacitaciones[i].costo+"<br>" +
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

       $scope.showToast = function(mensaje) {
           $mdToast.show(
               $mdToast.simple()
                   .textContent(mensaje)
                   .hideDelay(5000)
           );
       };

       $scope.buscarCapacitaciones = function () {
           if($scope.busqueda.atributo===$scope.opciones[1]) {
               $http.post('/api/capacitaciones/buscarFecha',$scope.busquedaFecha).then(
                   function (response) {
                       $scope.filtro = "Filtrando capacitaciones por fecha ("+$scope.mostrarFecha($scope.busquedaFecha.fechaInicio)+"-"+$scope.mostrarFecha($scope.busquedaFecha.fechaFinal)+")";
                       $scope.capacitaciones = response.data;
                   }
               ).catch(
                   function (error) {
                       $scope.showAlert(error.data.mensaje);
                   }
               );
           } else {
               $http.post('/api/capacitaciones/buscar',$scope.busqueda).then(
                   function (response) {
                       $scope.filtro = "Filtrando capacitaciones por "+$scope.busqueda.atributo+" con valor "+$scope.busqueda.valor;
                       $scope.capacitaciones = response.data;
                   }
               ).catch(
                   function (error) {
                       $scope.showAlert(error.data.mensaje);
                   }
               );
           }
       };

       $scope.mostrarFecha = function (fecha) {
           return fecha.getDate()+"/"+(fecha.getMonth()+1)+"/"+fecha.getFullYear();
       };

       $scope.agregarCapacitaciones = function () {
           if($scope.formData) {
               $http.post('/api/capacitaciones/upload', $scope.formData, {
                   headers: {'Content-Type': undefined }
               }).then(
                   function() {
                       $scope.formData = null;
                       $scope.mostrarTodos();
                   }
               ).catch(
                   function(error) {
                       $scope.formData = null;
                       $scope.mostrarTodos();
                       $scope.showAlert(error.data.mensaje);
                   }
               );
           } else {
               $scope.showAlert("Debe seleccionarse un archivo");
           }
       };

       $scope.uploadFile = function(files) {
           $scope.formData = new FormData();
           $scope.formData.append("excel", files[0]);
       };

       $scope.millisToFecha = function (millis) {
           var date = new Date(millis);
           return date.getDate()+"/"+(date.getMonth()+1)+"/"+date.getFullYear();
       };

       $scope.irAgregar = function () {
           $scope.agregar = true;
       };

       $scope.salirAgregar = function () {
           $scope.agregar = false;
       };

       $scope.agregarCapacitacion = function () {
           $http.post('/api/capacitaciones',$scope.capacitacion).then(function (response) {
               $scope.capacitaciones.push(response.data);
               $scope.capacitacion = {};
               $scope.agregar = false;
           }).catch(function (error) {
               $scope.showAlert(error.data.mensaje);
           });
       };

       $scope.irEditar = function (index) {
           if(!$scope.agregar) {
               $scope.editar = true;
               $scope.capacitacion = JSON.parse(JSON.stringify($scope.capacitaciones[index]));
               $scope.capacitacion.index = index
               $scope.capacitacion.fechaInicio = new Date($scope.capacitacion.fechaInicio);
               $scope.capacitacion.fechaFinal = new Date($scope.capacitacion.fechaFinal);
           }
       }

       $scope.salirEditar = function () {
           $scope.editar = false;
       }

       $scope.editarCapacitacion = function () {
           $http.put('/api/capacitaciones/'+$scope.capacitacion.id,$scope.capacitacion).then(function (response) {
               $scope.capacitaciones[$scope.capacitacion.index] = response.data;
               $scope.editar = false;
           }).catch(function (error) {
               $scope.showAlert(error.data.mensaje);
           });
       };

       $scope.eliminarCapacitacion = function(evento, index) {
           var confirm = $mdDialog.confirm()
               .title('¿Desea eliminar la capacitación?')
               .textContent('Se eliminará el registro de la capacitación y todas sus asistencias asociadas.')
               .ariaLabel('Eliminar capacitación')
               .targetEvent(evento)
               .ok('Eliminar')
               .cancel('Cancelar');
           $mdDialog.show(confirm).then(function() {
               $http.delete('/api/capacitaciones/'+$scope.capacitaciones[index].id).then(function (response) {
                   $scope.capacitaciones.splice(index,1);
               }).catch(function (error) {
                   $scope.showAlert('Ocurrió un error al eliminar la capacitación');
               });
           });
       };

       $scope.agregarAsistencias = function (evento, index) {
           $mdDialog.show({
               controller: 'AsistenciasCtrl',
               templateUrl: 'public/app/asistencias/agregarAsistencias.tpl.html',
               parent: angular.element(document.body),
               targetEvent: event,
               locals: {
                   capacitacion: $scope.capacitaciones[index]
               },
               clickOutsideToClose:true
           }).then(function (respuesta) {
               $scope.showToast(respuesta);
           });
       };

       $scope.verAsistencias = function (index) {
           $http.get('/api/capacitaciones/'+$scope.capacitaciones[index].id+'/asistencias').then(function (response) {
               $scope.descripcionCapacitacion = $scope.capacitaciones[index].descripcion
               $scope.idCapacitacion = $scope.capacitaciones[index].id
               $scope.mostrarAsistencias = true;
               $scope.asistencias = response.data;
           }).catch(function () {
               $scope.showAlert('No se pudo obtener las asistencias a la capacitación');
           });
       };

       $scope.ocultarAsistencias = function () {
           $scope.mostrarAsistencias = false;
           $scope.busquedaAsistencia = {};
       };

       $scope.filtrarAsistencias = function () {
           $http.post('/api/capacitaciones/'+$scope.idCapacitacion+'/asistencias',$scope.busquedaAsistencia).then(function (response) {
               $scope.asistencias = response.data;
           }).catch(function () {
               $scope.showAlert('No se pudo filtrar las asistencias de la capacitación');
           });
       };

       $scope.mostrarTodasAsistencias = function () {
           $http.get('/api/capacitaciones/'+$scope.idCapacitacion+'/asistencias').then(function (response) {
               $scope.asistencias = response.data;
           }).catch(function () {
               $scope.showAlert('No se pudo obtener las asistencias de la capacitación');
           });
       };
   });
})();