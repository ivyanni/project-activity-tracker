import TimelineChart from "./timeline";

let projects = [];

window.validateFields = function () {
    let result = true;
    $('.modal.show').find('.was-validated').find(':input[required]').each(function() {
        if(!($(this)[0].checkValidity())) {
            console.log($(this));
            $(this)[0].reportValidity();
            result = false;
            return false;
        }
    });
    return result;
};

window.updateProjects = function (async = true) {
    $('#pleaseWaitDialog').modal();
    $.ajax({
        type: "GET",
        dataType: "json",
        async: async,
        crossDomain: true,
        url: "http://localhost:3000/api/projects/",
        success: function(responseData, status, xhr) {
            console.log(responseData);
            $('#projects').empty();
            $('.project-select').empty();
            projects = [];
            for(let i = 0; i < responseData.length; i++) {
                let project = responseData[i];
                projects.push(project);
                $('#projects').append('<option value="' + project.id + '">' + project.name + '</option>');
                $('.project-select').append('<option value="' + project.id + '">' + project.name + '</option>');
            }
            updateLayers();
            load();
            $('#pleaseWaitDialog').modal('hide');
        },
        error: function(request, status, error) {
            console.log(request.responseText);
        }
    });
};

window.updateLayers = function() {
    let selectedProject = $('#projects').val();
    $('#layers').empty();
    for(let i = 0; i < projects.length; i++) {
        if(projects[i].id == selectedProject) {
            for (let j = 0; j < projects[i].layers.length; j++) {
                let layer = projects[i].layers[j];
                $('#layers').append('<option value="' + layer.id + '">' + layer.name + '</option>');
            }
        }
    }
};

window.load = function () {
    if($("#layers").val() === null) {
        return;
    }
    $('#buttonsCol').show();
    if( $('#chart').length ) {
        $("#chart").empty();
    } else {
        $("#row3col2").append('<div id="chart" ></div>');
    }
    $('#chart').hide();
    $.ajax({
        type: "GET",
        dataType: "json",
        async: false,
        crossDomain: true,
        url: "http://localhost:3000/api/layers/" + $("#layers").val() + '/',
        success: function (responseData, status, xhr) {
            console.log(responseData);
            $('#layerId').val(responseData.id);
            $('#layerName').val(responseData.name);
            window.layerFinishDate = responseData.finishDate;
            $('#finishDate').val(responseData.finishDate);
            $('#projectSelect').val(responseData.projectId);
            $('#projectId').val($('#projects option:selected').val());
            $('#projectName').val($('#projects option:selected').text());
            $('.stream-input').empty();
            $('#delPhase').empty();
            for(let i = 0; i < responseData.streams.length; i++) {
                $('.stream-input').append('<option value="'+ responseData.streams[i].id + '">'+ responseData.streams[i].name +'</option>')
            }
            for(let i = 0; i < responseData.phases.length; i++) {
                $('#delPhase').append('<option value="'+ responseData.phases[i].id + '">'+ responseData.phases[i].name +'</option>')
            }
            $('.dep-input').empty();
            for(let j = 0; j < responseData.streams.length; j++) {
                responseData.streams[j].events = [];
                responseData.streams[j].deliverables = [];
            }
            for(let i = 0; i < responseData.events.length; i++) {
                responseData.events[i].datetime = Date.parse(responseData.events[i].date + 'T' + responseData.events[i].time + 'Z');
                for(let j = 0; j < responseData.streams.length; j++) {
                    if(responseData.events[i].stream.id === responseData.streams[j].id) {
                        if(responseData.events[i].deliverable !== true) {
                            responseData.streams[j].events.push(responseData.events[i]);
                        } else {
                            responseData.streams[j].deliverables.push(responseData.events[i]);
                        }
                        break;
                    }
                }
            }
            $('#chart').show();
            new TimelineChart(chart, responseData, {
                enableLiveTimer: true,
                tip: function (d) {
                    if(d.completionDate !== null && d.completionDate !== undefined) {
                        return '<i>Completed on: ' + new Date(d.completionDate).toLocaleDateString() + '</i><br /><br />' + new Date(d.datetime).toLocaleDateString() + '<br /><b>' + d.name + '</b>';
                    } else {
                        return new Date(d.datetime).toLocaleDateString() + '<br /><b>' + d.name + '</b>';
                    }
                }
            }).onVizChange(e => console.log(e));
            for(let i = 0; i < responseData.streams.length; i++) {
                let curStream = responseData.streams[i];
                let streamId = 'stream'+curStream.id;
                $('.dep-input').append('<optgroup class="dep'+streamId+'" label="'+curStream.name+'">');
                let allEvents = curStream.events.concat(curStream.deliverables).sort(function(a,b) { return a.datetime - b.datetime });
                for(let j = 0; j < allEvents.length; j++) {
                    let curEvent = allEvents[j];
                    $('.dep' + streamId).append('<option value="'+ curEvent.id +'">'+curEvent.name+'</option>')
                }
            }
        },
        error: function (request, status, error) {
            console.log(request.responseText);
        }
    });
};

window.createLayer = function () {
    if(!validateFields())
        return;
    let projectId = $('#newProjectSelect').val();
    $('#newLayerModal').modal('hide');
    $('#pleaseWaitDialog').modal();
    let finishDate = $('#newFinishDate').val();
    let requestData = {
        projectId: projectId,
        name: $('#newLayerName').val(),
        finishDate: finishDate
    };
    $.ajax({
        type: "POST",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        dataType: 'json',
        data: JSON.stringify(requestData),
        crossDomain: true,
        crossOrigin: true,
        url: "http://localhost:3000/api/layers/",
        success: function (responseData, status, xhr) {
            console.log(responseData);
            $('#pleaseWaitDialog').modal('hide');
            updateProjects();
        },
        error: function (request, status, error) {
            console.log(request.responseText);
        }
    });
};

window.saveLayer = function () {
    if(!validateFields())
        return;
    let projectId = $('#projectSelect').val();
    let layerId = $('#layers').val();
    $('#layerModal').modal('hide');
    $('#pleaseWaitDialog').modal();
    let finishDate = $('#finishDate').val();
    let requestData = {
        projectId: projectId,
        name: $('#layerName').val(),
        finishDate: finishDate
    };
    console.log(requestData);
    $.ajax({
        type: "POST",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        dataType: 'json',
        data: JSON.stringify(requestData),
        crossDomain: true,
        crossOrigin: true,
        url: "http://localhost:3000/api/layers/" + layerId,
        success: function (responseData, status, xhr) {
            console.log(responseData);
            $('#pleaseWaitDialog').modal('hide');
            updateProjects();
        },
        error: function (request, status, error) {
            console.log(request.responseText);
        }
    });
};

window.createProject = function () {
    if(!validateFields())
        return;
    $('#newProjectModal').modal('hide');
    $('#pleaseWaitDialog').modal();
    $.ajax({
        type: "POST",
        crossDomain: true,
        crossOrigin: true,
        url: "http://localhost:3000/api/projects/?name=" + $("#newProjectName").val(),
        success: function (responseData, status, xhr) {
            $('#pleaseWaitDialog').modal('hide');
            updateProjects();
        },
        error: function (request, status, error) {
            console.log(request.responseText);
        }
    });
};

window.saveProject = function () {
    if(!validateFields())
        return;
    $('#projectModal').modal('hide');
    $('#pleaseWaitDialog').modal();
    let requestData = {
        name: $('#projectName').val()
    };
    console.log(requestData);
    $.ajax({
        type: "POST",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        dataType: 'json',
        data: JSON.stringify(requestData),
        crossDomain: true,
        crossOrigin: true,
        url: "http://localhost:3000/api/projects/" + $("#projectId").val(),
        success: function (responseData, status, xhr) {
            $('#pleaseWaitDialog').modal('hide');
            updateProjects();
        },
        error: function (request, status, error) {
            console.log(request.responseText);
        }
    });
};

window.deleteProject = function () {
    $('#projectModal').modal('hide');
    $('#pleaseWaitDialog').modal();
    $.ajax({
        type: "DELETE",
        crossDomain: true,
        crossOrigin: true,
        url: "http://localhost:3000/api/projects/" + $("#projectId").val(),
        success: function (responseData, status, xhr) {
            $('#pleaseWaitDialog').modal('hide');
            updateProjects();
        },
        error: function (request, status, error) {
            console.log(request.responseText);
        }
    });
};

window.deleteLayer = function () {
    $('#layerModal').modal('hide');
    $('#pleaseWaitDialog').modal();
    $.ajax({
        type: "DELETE",
        crossDomain: true,
        crossOrigin: true,
        url: "http://localhost:3000/api/layers/" + $("#layerId").val(),
        success: function (responseData, status, xhr) {
            $('#pleaseWaitDialog').modal('hide');
            updateProjects();
        },
        error: function (request, status, error) {
            console.log(request.responseText);
        }
    });
};

window.deleteStream = function () {
    $('#newStreamModal').modal('hide');
    $('#pleaseWaitDialog').modal();
    $.ajax({
        type: "DELETE",
        crossDomain: true,
        crossOrigin: true,
        url: "http://localhost:3000/api/streams/" + $("#streamId").val(),
        success: function (responseData, status, xhr) {
            load();
            $('#pleaseWaitDialog').modal('hide');
        },
        error: function (request, status, error) {
            console.log(request.responseText);
        }
    });
};

window.deleteEvent = function () {
    $('#newEventModal').modal('hide');
    $('#pleaseWaitDialog').modal();
    $.ajax({
        type: "DELETE",
        crossDomain: true,
        crossOrigin: true,
        url: "http://localhost:3000/api/events/" + $("#eventId").val(),
        success: function (responseData, status, xhr) {
            load();
            $('#pleaseWaitDialog').modal('hide');
        },
        error: function (request, status, error) {
            console.log(request.responseText);
        }
    });
};

window.deleteDeliverable = function () {
    $('#newDeliverableModal').modal('hide');
    $('#pleaseWaitDialog').modal();
    $.ajax({
        type: "DELETE",
        crossDomain: true,
        crossOrigin: true,
        url: "http://localhost:3000/api/events/" + $("#deliverableId").val(),
        success: function (responseData, status, xhr) {
            load();
            $('#pleaseWaitDialog').modal('hide');
        },
        error: function (request, status, error) {
            console.log(request.responseText);
        }
    });
};

window.deletePhase = function () {
    $('#newPhaseModal').modal('hide');
    $('#pleaseWaitDialog').modal();
    $.ajax({
        type: "DELETE",
        crossDomain: true,
        crossOrigin: true,
        url: "http://localhost:3000/api/phases/" + $("#phaseId").val(),
        success: function (responseData, status, xhr) {
            load();
            $('#pleaseWaitDialog').modal('hide');
        },
        error: function (request, status, error) {
            console.log(request.responseText);
        }
    });
};

window.createStream = function () {
    if(!validateFields())
        return;
    $('#newStreamModal').modal('hide');
    $('#pleaseWaitDialog').modal();
    let requestData = {
        layerId: $("#layers").val(),
        name: $('#streamName').val(),
        order: $('#streamOrder').val()
    };
    console.log(requestData);
    $.ajax({
        type: "POST",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        dataType: 'json',
        data: JSON.stringify(requestData),
        crossDomain: true,
        crossOrigin: true,
        url: "http://localhost:3000/api/streams/",
        success: function (responseData, status, xhr) {
            console.log(responseData);
            load();
            $('#pleaseWaitDialog').modal('hide');
        },
        error: function (request, status, error) {
            console.log(request.responseText);
        }
    });
};

window.createPhase = function () {
    if(!validateFields())
        return;
    $('#newPhaseModal').modal('hide');
    $('#pleaseWaitDialog').modal();
    let startDate = Date.parse($('#phaseStartDate').val().toString());
    let endDate = Date.parse($('#phaseEndDate').val().toString());
    let startDateString = new Date(startDate).toISOString();
    let endDateString = new Date(endDate).toISOString();
    let requestData = {
        layerId: $("#layers").val(),
        name: $('#phaseName').val(),
        startDate: startDateString.substring(0, startDateString.indexOf('T')),
        endDate: endDateString.substring(0, endDateString.indexOf('T')),
        color: $('#phaseColor').val()
    };
    console.log(requestData);
    $.ajax({
        type: "POST",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        dataType: 'json',
        data: JSON.stringify(requestData),
        crossDomain: true,
        crossOrigin: true,
        url: "http://localhost:3000/api/phases/",
        success: function (responseData, status, xhr) {
            console.log(responseData);
            load();
            $('#pleaseWaitDialog').modal('hide');
        },
        error: function (request, status, error) {
            console.log(request.responseText);
        }
    });
};

window.savePhase = function () {
    if(!validateFields())
        return;
    $('#newPhaseModal').modal('hide');
    $('#pleaseWaitDialog').modal();
    let startDate = Date.parse($('#phaseStartDate').val().toString());
    let endDate = Date.parse($('#phaseEndDate').val().toString());
    let startDateString = new Date(startDate).toISOString();
    let endDateString = new Date(endDate).toISOString();
    let requestData = {
        layerId: $("#layers").val(),
        name: $('#phaseName').val(),
        startDate: startDateString.substring(0, startDateString.indexOf('T')),
        endDate: endDateString.substring(0, endDateString.indexOf('T')),
        color: $('#phaseColor').val()
    };
    console.log(requestData);
    $.ajax({
        type: "POST",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        dataType: 'json',
        data: JSON.stringify(requestData),
        crossDomain: true,
        crossOrigin: true,
        url: "http://localhost:3000/api/phases/" + $('#phaseId').val(),
        success: function (responseData, status, xhr) {
            console.log(responseData);
            load();
            $('#pleaseWaitDialog').modal('hide');
        },
        error: function (request, status, error) {
            console.log(request.responseText);
        }
    });
};

window.saveStream = function () {
    if(!validateFields())
        return;
    $('#newStreamModal').modal('hide');
    $('#pleaseWaitDialog').modal();
    let requestData = {
        layerId: $("#layers").val(),
        name: $('#streamName').val(),
        order: $('#streamOrder').val()
    };
    console.log(requestData);
    $.ajax({
        type: "POST",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        dataType: 'json',
        data: JSON.stringify(requestData),
        crossDomain: true,
        crossOrigin: true,
        url: "http://localhost:3000/api/streams/" + $('#streamId').val(),
        success: function (responseData, status, xhr) {
            console.log(responseData);
            load();
            $('#pleaseWaitDialog').modal('hide');
        },
        error: function (request, status, error) {
            console.log(request.responseText);
        }
    });
};

window.saveEvent = function () {
    if(!validateFields())
        return;
    $('#newEventModal').modal('hide');
    $('#pleaseWaitDialog').modal();
    let dateTime = $('#eventDate').val().toString();
    let compDate = $('#eventCompletionDate').val();
    let dateTimeString = new Date(dateTime).toISOString();
    let topLevelEvents = $('#dependencies').val();
    let requestData = {
        layerId: $("#layers").val(),
        name: $('#eventName').val(),
        date: dateTimeString.substring(0, dateTimeString.indexOf('T')),
        completionDate: compDate,
        stream: {
            id: $('#eventStream').val()
        },
        topLevelEvents: topLevelEvents,
        description: $('#eventDescription').val(),
        deliverable: false
    };
    console.log(requestData);
    $.ajax({
        type: "POST",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        dataType: 'json',
        data: JSON.stringify(requestData),
        crossDomain: true,
        crossOrigin: true,
        url: "http://localhost:3000/api/events/" + $('#eventId').val(),
        success: function (responseData, status, xhr) {
            console.log(responseData);
            load();
            $('#pleaseWaitDialog').modal('hide');
        },
        error: function (request, status, error) {
            console.log(request.responseText);
        }
    });
};

window.saveDeliverable = function () {
    if(!validateFields())
        return;
    $('#newDeliverableModal').modal('hide');
    $('#pleaseWaitDialog').modal();
    let compDate = $('#delCompletionDate').val();
    let topLevelEvents = $('#dependencies2').val();
    let requestData = {
        layerId: $("#layers").val(),
        name: $('#delName').val(),
        stream: {
            id: $('#delStream').val()
        },
        description: $('#delDescription').val(),
        phase: {
            id: $('#delPhase').val()
        },
        completionDate: compDate,
        topLevelEvents: topLevelEvents,
        deliverable: true
    };
    console.log(requestData);
    $.ajax({
        type: "POST",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        dataType: 'json',
        data: JSON.stringify(requestData),
        crossDomain: true,
        crossOrigin: true,
        url: "http://localhost:3000/api/events/" + $('#deliverableId').val(),
        success: function (responseData, status, xhr) {
            console.log(responseData);
            load();
            $('#pleaseWaitDialog').modal('hide');
        },
        error: function (request, status, error) {
            console.log(request.responseText);
        }
    });
};

window.createNewEvent = function () {
    if(!validateFields())
        return;
    $('#newEventModal').modal('hide');
    $('#pleaseWaitDialog').modal();
    let compDate = $('#eventCompletionDate').val();
    let dateTime = $('#eventDate').val().toString();
    let dateTimeString = new Date(dateTime).toISOString();
    let topLevelEvents = $('#dependencies').val();
    let requestData = {
        layerId: $("#layers").val(),
        name: $('#eventName').val(),
        date: dateTimeString.substring(0, dateTimeString.indexOf('T')),
        stream: {
            id: $('#eventStream').val()
        },
        description: $('#eventDescription').val(),
        topLevelEvents: topLevelEvents,
        completionDate: compDate,
        deliverable: false
    };
    console.log(requestData);
    $.ajax({
        type: "POST",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        dataType: 'json',
        data: JSON.stringify(requestData),
        crossDomain: true,
        crossOrigin: true,
        url: "http://localhost:3000/api/events/",
        success: function (responseData, status, xhr) {
            console.log(responseData);
            load();
            $('#pleaseWaitDialog').modal('hide');
        },
        error: function (request, status, error) {
            console.log(request.responseText);
        }
    });
};

window.createNewDeliverable = function () {
    if(!validateFields())
        return;
    $('#newDeliverableModal').modal('hide');
    $('#pleaseWaitDialog').modal();
    let compDate = $('#delCompletionDate').val();
    let topLevelEvents = $('#dependencies2').val();
    let requestData = {
        layerId: $("#layers").val(),
        name: $('#delName').val(),
        stream: {
            id: $('#delStream').val()
        },
        phase: {
            id: $('#delPhase').val()
        },
        description: $('#delDescription').val(),
        topLevelEvents: topLevelEvents,
        completionDate: compDate,
        deliverable: true
    };
    console.log(requestData);
    $.ajax({
        type: "POST",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        dataType: 'json',
        data: JSON.stringify(requestData),
        crossDomain: true,
        crossOrigin: true,
        url: "http://localhost:3000/api/events/",
        success: function (responseData, status, xhr) {
            console.log(responseData);
            load();
            $('#pleaseWaitDialog').modal('hide');
        },
        error: function (request, status, error) {
            console.log(request.responseText);
        }
    });
};