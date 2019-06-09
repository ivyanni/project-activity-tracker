import $ from 'jquery';
import 'bootstrap';
import './scss/custom.scss';
import './css/index.css';
import './css/fontawesome-all.min.css';
import './functions';

window.addEventListener('resize', function () { load(); });
$( document ).ready(function() {
    load();
});

let html = require('./modals.html');
$("#app").append(html);

$(function () {
    $('[data-toggle="tooltip"]').tooltip()
});

updateProjects();
$("#app").append('<nav class="navbar static-top navbar-dark bg-primary"><div><a href="/" class="navbar-brand d-inline-block">Project Activity Tracker</a></div></nav>');
$("#app").append('<div class="container">');
$(".container").append('<div class="row"><div class="col" id="row1">');
$(".container").append('<div class="row justify-content-md-center" id="row2">');
// $("#row2").append('<div class="col" id="row2col1">');
// $("#row2").append('<div class="col" id="row2col2">');
$(".container").append('<div class="row justify-content-md-center" id="row3">');
$("#row3").append('<div class="col-2" id="row3col1" style="text-align: left;">');
$("#row3").append('<div class="col-10" id="row3col2">');
//$("#app").append('<img width="200" height="100" src="https://mms.businesswire.com/media/20171214005373/en/510274/22/NTC_Logo_Horiz_CMYK.jpg"/>');
$('#row2').append('<div class="col"><div class="input-group mb-3">' +
    '<div class="input-group-prepend"><span class="input-group-text" id="select-addon2">Project</span></div>' +
    '<select class="custom-select" id="projects" /><div class="input-group-append"><button class="btn btn-outline-primary" id="change-project-btn"><i class="fas fa-edit"></i></button><button class="btn btn-outline-primary" id="new-project-btn"><i class="fas fa-plus"></i></button></div></div></div>');
$("#row2").append('<div class="col"><div class="input-group mb-3">\n' +
    '<div class="input-group-prepend">\n' +
    '    <span class="input-group-text" id="select-addon">Layer</span>\n' +
    '</div>' +
    '<select class="custom-select" id="layers" /><div class="input-group-append"><button class="btn btn-outline-primary" id="change-layer-btn"><i class="fas fa-edit"></i></button><button class="btn btn-outline-primary" id="new-layer-btn"><i class="fas fa-plus"></i></button></div>' +
    '</div></div>');
// <div className="col"><div className="input-group mb-3"><input type="text" className="form-control" id="layerName" placeholder="New layer name" /><div className="input-group-append"><button className="btn btn-primary" onClick="createLayer()"><i className="fas fa-plus-circle"></i></button></div></div></div>
$('#row3col1').append('<div id="buttonsCol" class="btn-group-vertical" style="width: 100%;" role="group"></div>');
$('#buttonsCol').hide();
$("#buttonsCol").append('<button id="newStreamBtn" class="btn btn-outline-primary">New stream</button>');
$("#buttonsCol").append('<button id="newPhaseBtn" class="btn btn-outline-primary">New phase</button>');
$("#buttonsCol").append('<button id="newDeliverableBtn" class="btn btn-outline-primary">New deliverable</button>');
$("#buttonsCol").append('<button id="newEventBtn" class="btn btn-outline-primary">New event</button>');
$('#projects').change(function() {
    updateLayers();
    load();
});
$('#layers').change(load);
$('#newPhaseBtn').click(function() {
    $('#newPhaseModalTitle').text('New phase');
    $('#phaseName').val('');
    $('#phaseStartDate').val('');
    $('#phaseEndDate').val('');
    $('#phaseColor').val('');
    $('.new-modal-btn').show();
    $('.edit-modal-btn').hide();
    $('#newPhaseModal').modal('show');
});
$('#newStreamBtn').click(function () {
    $('#newStreamModalTitle').text('New stream');
    $('#streamName').val('');
    $('#streamId').val('');
    $('#streamOrder').val('');
    $('.new-modal-btn').show();
    $('.edit-modal-btn').hide();
    $('#newStreamModal').modal('show');
});
$('#newEventBtn').click(function () {
    $('#newEventModalTitle').text('New event');
    $('#eventName').val('');
    $('#eventId').val('');
    $('#eventDate').val('');
    $('#eventTime').val('');
    $('#eventDescription').val('');
    $('#eventStream').val('');
    $('#dependencies').val('');
    $('#eventCompletionDate').val('');
    $('.new-modal-btn').show();
    $('.edit-modal-btn').hide();
    $('#newEventModal').modal('show');
});
$('#newDeliverableBtn').click(function () {
    $('#newDeliverableModalTitle').text('New deliverable');
    $('#delName').val('');
    $('#delPhase').val('');
    $('#delStream').val('');
    $('#delDescription').val('');
    $('#delCompletionDate').val('');
    $('#deliverableId').val('');
    $('#dependencies2').val('');
    $('.new-modal-btn').show();
    $('.edit-modal-btn').hide();
    $('#newDeliverableModal').modal('show');
});
$('#change-layer-btn').click(function () {
    $('#layerModalTitle').text('Edit layer');
    $('.new-modal-btn').hide();
    $('.edit-modal-btn').show();
    $('#layerModal').modal('show');
});
$('#new-layer-btn').click(function () {
    $('#newLayerModalTitle').text('New layer');
    $('#newLayerName').val('');
    $('#newFinishDate').val('');
    $('#newProjectSelect').val('');
    $('.new-modal-btn').show();
    $('.edit-modal-btn').hide();
    $('#newLayerModal').modal('show');
});
$('#change-project-btn').click(function () {
    $('#projectModalTitle').text('Edit project');
    $('.new-modal-btn').hide();
    $('.edit-modal-btn').show();
    $('#projectModal').modal('show');
});
$('#new-project-btn').click(function () {
    $('#newProjectModalTitle').text('New project');
    $('#newProjectName').val('');
    $('.new-modal-btn').show();
    $('.edit-modal-btn').hide();
    $('#newProjectModal').modal('show');
});
$("#app").append('<nav class="navbar fixed-bottom navbar-dark bg-primary py-1"><a class="navbar-brand" href="#"></a><span class="navbar-text"> </span></nav>');