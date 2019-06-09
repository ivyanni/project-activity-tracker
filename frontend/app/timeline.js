import * as d3 from 'd3'
import d3Tip from 'd3-tip'
import './css/timeline.css'

class Timeline {
    constructor(element, data, opts) {
        let self = this;
        element.classList.add('timeline-chart');

        let options = this.extendOptions(opts);

        let phaseById = d3.map(data.phases, d => d.id);
        let lastPhaseFinishdate = 0;
        for(let i = 0; i < data.phases.length; i++) {
            data.phases[i].longStartDate = Date.parse(data.phases[i].startDate + 'T00:00');
            data.phases[i].longEndDate = Date.parse(data.phases[i].endDate + 'T00:00');
            if(data.phases[i].longEndDate > lastPhaseFinishdate) {
                lastPhaseFinishdate = data.phases[i].longEndDate;
            }
            if(data.phases[i].color !== null && data.phases[i].color !== undefined)
                data.phases[i].color += '66';
            else data.phases[i].color = '#2b2b2b66'
        }
        for(let i = 0; i < data.events.length; i++) {
            let event = data.events[i];
            event.datetime = Date.parse(event.date + 'T00:00');
            if(event.completionDate !== null)
                event.completionDate = event.completionDate + 'T00:00';
        }
        for(let i = 0; i < data.streams.length; i++) {
            let curStream = data.streams[i];
            for (let j = 0; j < curStream.deliverables.length; j++) {
                curStream.deliverables[j].datetime = phaseById.get(curStream.deliverables[j].phase.id).longEndDate;
            }
        }
        let timeDiff = 0;
        for(let i = 0; i < data.events.length; i++) {
            let event = data.events[i];
            let nowdate = Date.parse(new Date().toISOString());
            if(event.completionDate !== null) {
                if(Date.parse(event.completionDate) > event.datetime) {
                    timeDiff += Date.parse(event.completionDate) - event.datetime;
                } else {
                    timeDiff -= event.datetime - Date.parse(event.completionDate);
                }
            } else {
                if(event.datetime < nowdate) {
                    timeDiff += nowdate - event.datetime;
                }
            }
        }

        let minDt = d3.min(data.phases, this.getPointMinDt);
        let maxDt = d3.max(data.phases, this.getPointMaxDt);

        let elementWidth = options.width || element.clientWidth;
        let elementHeight = options.height || element.clientHeight;

        let margin = {
            top: 0,
            right: 70,
            bottom: 0,
            left: 0
        };

        let phasesWidth = 40;
        let width = elementWidth - phasesWidth - margin.left - margin.right;
        let height = elementHeight - margin.top - margin.bottom;

        let groupHeight = (options.hideGroupLabels)? 0: 40;

        let y = d3.time.scale()
            .domain([minDt, maxDt])
            .range([groupHeight, height]);

        let yAxis = d3.svg.axis()
            .scale(y)
            .orient('right')
            .tickSize(width);

        let zoom = d3.behavior.zoom()
            .y(y)
            .on('zoom', zoomed)
            .on('zoomend', zoomEnded);

        let nodeById = d3.map(data.events, d => d.id);

        let svg = d3.select(element).append('svg')
            .attr('width', width + phasesWidth + margin.left + margin.right)
            .attr('height', height + margin.top + margin.bottom)
            .append('g')
            //.attr('transform', 'translate(' + phasesWidth +', 0)')
            .call(zoom);

        self.phaseStartLine = svg.selectAll('.phase-line').data(data.phases).enter().append('line')
            .attr('clip-path', 'url(#chart-content)')
            .style('stroke', d => d.color)
            .style('stroke-width', '2px')
            .attr('x1', 0)
            .attr('x2', width + phasesWidth)
            .attr('y1', d => y(d.longStartDate))
            .attr('y2', d => y(d.longStartDate));

        self.phaseEndLine = svg.selectAll('.phase-line').data(data.phases).enter().append('line')
            .attr('clip-path', 'url(#chart-content)')
            .style('stroke', d => d.color)
            .style('stroke-width', '2px')
            .attr('x1', 0)
            .attr('x2', width + phasesWidth)
            .attr('y1', d => y(d.longEndDate))
            .attr('y2', d => y(d.longEndDate));

        self.phasearea = svg.selectAll('.phase-area').data(data.phases).enter().append('rect')
            .attr('clip-path', 'url(#chart-content)')
            .style('fill', d => d.color)
            .attr('x', 0)
            .attr('y', 0)
            .attr('height', d => y(d.longEndDate) - y(d.longStartDate))
            .attr('width', width + phasesWidth);

        self.phasetexts = svg.selectAll('.phase-area')
            .data(data.phases)
            .enter()
            .append('text')
            .attr('class', 'phase-text')
            .attr('clip-path', 'url(#chart-content)')
            .attr('dx', '1.25em')
            .attr('y', d => y(d.longStartDate))
            .attr('dy', '0.5em')
            //.attr('dx', '1px')
            .text(d => d.name)
            .on("click", function(d) {
                $('#newPhaseModalTitle').text('Edit phase');
                $('#phaseName').val(d.name);
                $('#phaseId').val(d.id);
                $('#phaseStartDate').val(d.startDate);
                $('#phaseEndDate').val(d.endDate);
                $('#phaseColor').val(d.color.substr(0, 7));
                $('.new-modal-btn').hide();
                $('.edit-modal-btn').show();
                $('#newPhaseModal').modal('show');
            });

        svg.append('defs')
            .append('clipPath')
            .attr('id', 'chart-content')
            .append('rect')
            .attr('x', 0)
            .attr('y', /*0*/groupHeight)
            .attr('height', height - groupHeight)
            .attr('width', width + phasesWidth/*- groupWidth*/);

        svg.append('rect')
            .attr('class', 'chart-bounds')
            .attr('x', phasesWidth)
            .attr('y', /*0*/groupHeight)
            .attr('height', height - groupHeight)
            .attr('width', width /*- groupWidth*/);

        svg.append('g')
            .attr('class', 'y axis')
           // .attr('x', phasesWidth)
            .attr('transform', 'translate('+ phasesWidth + ',0)')
            .call(yAxis);

        let finishdate = window.layerFinishDate;
        if (options.enableLiveTimer) {
            self.now = svg.append('line')
                .attr('clip-path', 'url(#chart-content)')
                .attr('class', 'vertical-marker now')
                .attr("x1", width + phasesWidth)
                .attr("y2", 0);
            self.finish = svg.append('line')
                .attr('clip-path', 'url(#chart-content)')
                .attr('class', 'vertical-marker finish')
                .attr("x1", width + phasesWidth)
                .attr("y2", 0);
            self.lastPhase = svg.append('line')
                .attr('clip-path', 'url(#chart-content)')
                .attr('class', 'vertical-marker last-phase')
                .attr("x1", width + phasesWidth)
                .attr("y2", 0);
            self.predict = svg.append('line')
                .attr('clip-path', 'url(#chart-content)')
                .attr('class', 'vertical-marker predict')
                .attr("x1", width + phasesWidth)
                .attr("y2", 0);
        }

        let groupWidth = width / data.streams.length;

        let groupSection = svg.selectAll('.group-section')
            .data(data.streams)
            .enter()
            .append('line')
            .attr('class', 'group-section')
            .attr('x1', (d, i) => {
                return groupWidth * (i + 1) + phasesWidth;
            })
            .attr('x2', (d, i) => {
                return groupWidth * (i + 1) + phasesWidth;
            })
            .attr('y1', 0)
            .attr('y2', height);

        if (!options.hideGroupLabels) {
            let groupLabels = svg.selectAll('.group-label')
                .data(data.streams)
                .enter()
                .append('text')
                .attr('class', 'group-label')
                .attr('x', (d, i) => {
                    return (groupWidth * i) + phasesWidth + 10;
                })
                .attr('y', 0)
                //.attr('dy', '1.5em')
                .text(d => d.name)
                .style('font-size', function(d) {
                    let streamWidth = groupWidth * data.streams.length;
                    d.fontsize = Math.min(streamWidth / this.getComputedTextLength() * 2.2, 16);
                    return Math.min(streamWidth / this.getComputedTextLength() * 2.2, 16) + "px";
                })
                .attr('dy', d => {
                    let fontSize = d.fontsize / 16;
                    return (1.5 / fontSize) + "em";
                })
                .on("click", function(d) {
                    $('#newStreamModalTitle').text('Edit stream');
                    $('#streamName').val(d.name);
                    $('#streamId').val(d.id);
                    $('#streamOrder').val(d.order);
                    $('.new-modal-btn').hide();
                    $('.edit-modal-btn').show();
                    $('#newStreamModal').modal('show');
                });

            let lineSection = svg.append('line').attr('class', 'group-section').attr('x1', 0).attr('x2', width + phasesWidth).attr('y1', groupHeight).attr('y2', groupHeight);
        }
        let lineSection = svg.append('line').attr('class', 'group-section').attr('x1', phasesWidth).attr('x2', phasesWidth).attr('y1', 0).attr('y2', height);

        var lineFunction = d3.svg.line()
            .x(function(d) {
                return d.x;
            })
            .y(function(d) {
                return y(d.datetime);
            })
            .interpolate("linear");

        svg.append("svg:defs").selectAll("marker")
            .data(["end"])      // Different link/path types can be defined here
            .enter().append("svg:marker")    // This section adds in the arrows
            .attr("id", String)
            .attr("viewBox", "0 -5 10 10")
            .attr("refX", 15)
            .attr("refY", 0)
            .attr("markerWidth", 10)
            .attr("markerHeight", 10)
            .attr("orient", "auto")
            .append("svg:path")
            .attr("d", "M0,-5L10,0L0,5");

        let lineGraph = [];
        let datas = [];
        for(let i = 0; i < data.events.length; i++) {
            let node = data.events[i];
            if(node.topLevelEvents !== null) {
                for (let j = 0; j < node.topLevelEvents.length; j++) {
                    let tle = nodeById.get(node.topLevelEvents[j]);
                    datas.push([tle, node]);

                    lineGraph.push(svg.append("path")
                        .attr('clip-path', 'url(#chart-content)')
                        .attr("class", "path")
                        .attr("marker-end", "url(#end)")
                        .attr("fill", "none"));
                        //.attr("d", lineFunction(datas[datas.length - 1])));
                }
            }
        }

        let groupDotItems = svg.selectAll('.group-dot-item')
            .data(data.streams)
            .enter()
            .append('g')
            .attr('clip-path', 'url(#chart-content)')
            .attr('class', 'nodes')
            .attr('transform', (d, i) => `translate(${groupWidth * i + phasesWidth}, 0)`);

        let del = groupDotItems
            .selectAll('.deldot')
            .data(d => d.deliverables)
            .enter()
            .append('circle')
            .attr('class', withCustom('deldot'))
            .attr('cx', function(d, i, j) {
                d.x = groupWidth * j + groupWidth / 2 + phasesWidth;
                return groupWidth / 2;
            })
            .attr('cy', d => {
                d.y = y(d.datetime);
                return d.y;
            })
            .attr('r', 6)
            .style('fill', function(d) {
                let nowdate = Date.parse(new Date().toISOString());
                if(d.completionDate === null || d.completionDate === undefined) {
                    if(nowdate > d.datetime)
                        return "#EC0808";
                    else return "#2b2b2b"
                } else {
                    if(new Date(d.completionDate) <= d.datetime) {
                        return "#008000";
                    } else {
                        return "#FFD700";
                    }
                }
            })
            .on("click", function(d) {
                $('#newDeliverableModalTitle').text('Edit deliverable');
                $('#delName').val(d.name);
                $('#delPhase').val(d.phase.id);
                $('#delStream').val(d.stream.id);
                $('#delDescription').val(d.description);
                $('#deliverableId').val(d.id);
                let completeDisabled = false;
                $('#dependencies2').find('option').show();
                $('#dependencies2').find('option[value="'+d.id+'"]').hide();
                $('#dependencies2').find('option').each(function(i) {
                    if(nodeById.get(this.value).datetime > d.datetime) {
                        $( this ).hide();
                    }
                });
                if (d.topLevelEvents !== null ) {
                    let tle = [];
                    for (let i = 0; i < d.topLevelEvents.length; i++) {
                        tle.push(d.topLevelEvents[i]);
                        if(nodeById.get(d.topLevelEvents[i]).completionDate === null
                            || nodeById.get(d.topLevelEvents[i]).completionDate === undefined) {
                            completeDisabled = true;
                        }
                    }
                    $('#dependencies2').val(tle);
                }
                $('#delCompletionDate').attr("disabled", completeDisabled);
                if(d.completionDate !== null) {
                    $('#delCompletionDate').val(d.completionDate.substring(0, d.completionDate.indexOf('T')));
                } else {
                    $('#delCompletionDate').val('');
                }
                $('.new-modal-btn').hide();
                $('.edit-modal-btn').show();
                $('#newDeliverableModal').modal('show');
            });

        let dots = groupDotItems
            .selectAll('.dot')
            .data(d => d.events)
            .enter()
            .append('circle')
            .attr('class', withCustom('dot'))
            .attr('cx', function(d, i, j) {
                d.x = groupWidth * j + groupWidth / 2 + phasesWidth;
                return groupWidth / 2;
            })
            .attr('cy', d => {
                d.y = y(d.datetime);
                return d.y;
            })
            .attr('r', 7)
            .style('fill', function(d) {
                let nowdate = Date.parse(new Date().toISOString());
                if(d.completionDate === null || d.completionDate === undefined) {
                    if(nowdate > d.datetime)
                        return "#EC0808";
                    else return "#2b2b2b"
                } else {
                    if(new Date(d.completionDate) <= new Date(d.date)) {
                        return "#008000";
                    } else {
                        return "#FFD700";
                    }
                }
            })
            .on("click", function(d) {
                $('#newEventModalTitle').text('Edit event');
                $('#eventName').val(d.name);
                $('#eventId').val(d.id);
                $('#eventDate').val(d.date);
                $('#eventDescription').val(d.description);
                $('#eventStream').val(d.stream.id);
                let completeDisabled = false;
                $('#dependencies').find('option').show();
                $('#dependencies').find('option[value="'+d.id+'"]').hide();
                $('#dependencies').find('option').each(function(i) {
                    if(nodeById.get(this.value).datetime > d.datetime) {
                        $( this ).hide();
                    }
                });
                if (d.topLevelEvents !== null ) {
                    let tle = [];
                    for (let i = 0; i < d.topLevelEvents.length; i++) {
                        tle.push(d.topLevelEvents[i]);
                        if(nodeById.get(d.topLevelEvents[i]).completionDate === null
                            || nodeById.get(d.topLevelEvents[i]).completionDate === undefined) {
                            completeDisabled = true;
                        }
                    }
                    $('#dependencies').val(tle);
                }
                if(d.completionDate !== null) {
                    $('#eventCompletionDate').val(d.completionDate.substring(0, d.completionDate.indexOf('T')));
                } else {
                    $('#eventCompletionDate').val('');
                }
                $('#eventCompletionDate').attr("disabled", completeDisabled);
                //$('#dependencies').val(d.topLevelEvents);
                $('.new-modal-btn').hide();
                $('.edit-modal-btn').show();
                $('#newEventModal').modal('show');
            });

        if (options.tip) {
            d3.tip = d3Tip;
            let tip = d3.tip().attr('class', 'd3-tip').html(options.tip);
            svg.call(tip);
            dots.on('mouseover', tip.show).on('mouseout', tip.hide)
            del.on('mouseover', tip.show).on('mouseout', tip.hide)
        }

        zoomed();

        if (options.enableLiveTimer) {
            setInterval(updateNowMarker, options.timerTickInterval);
        }

        function updateNowMarker() {
            let nowY = y(new Date());
            let timeString = Date.parse(finishdate + "T00:00");
            let finishY = y(new Date(timeString));
            let endY = y(new Date(lastPhaseFinishdate));
            let predictDate = lastPhaseFinishdate + timeDiff;
            let predictY = y(new Date(predictDate));
            self.now.attr('y1', nowY).attr('y2', nowY);
            self.finish.attr('y1', finishY).attr('y2', finishY);
            self.predict.attr('y1', predictY).attr('y2', predictY);
            self.lastPhase.attr('y1', endY).attr('y2', endY);
        }

        function updatePhaseMarkers() {
            for(let i = 0; i < lineGraph.length; i++) {
                lineGraph[i].attr("d", lineFunction(datas[i]));
            }
            self.phasearea
                .attr('y', d => y(d.longStartDate))
                .attr('height', d => y(d.longEndDate) - y(d.longStartDate));
            self.phaseStartLine
                .attr('y1', d => y(d.longStartDate))
                .attr('y2', d => y(d.longStartDate));
            self.phaseEndLine
                .attr('y1', d => y(d.longEndDate))
                .attr('y2', d => y(d.longEndDate));
            //let phaseHeight;
            function computeFontsize(phaseHeight) {
                return Math.min(1.7 * phaseHeight, 16);
            }

            self.phasetexts
                .attr('y', d => {
                    return  y(d.longStartDate);
                })
                .attr('dx', d => {
                    let phaseHeight = (y(d.longEndDate) - y(d.longStartDate)) / 10;
                    let fontSize = computeFontsize(phaseHeight) / 16;
                    return (1.25 / fontSize) + "em";
                })
                .style('font-size', function(d) {
                    let phaseHeight = (y(d.longEndDate) - y(d.longStartDate)) / 10;
                    return computeFontsize(phaseHeight) + "px";
                });
        }

        function withCustom(defaultClass) {
            return d => d.customClass ? [d.customClass, defaultClass].join(' ') : defaultClass
        }

        function zoomEnded() {
        }

        function zoomed() {
            if (self.onVizChangeFn && d3.event) {
                self.onVizChangeFn.call(self, {
                    scale: d3.event.scale,
                    translate: d3.event.translate,
                    domain: y.domain()
                });
            }
            if (options.enableLiveTimer) {
                updateNowMarker();
            }
            updatePhaseMarkers();
            svg.select('.y.axis').call(yAxis);
            svg.selectAll('circle.dot').attr('cy', d => y(d.datetime));
            svg.selectAll('circle.deldot').attr('cy', d => y(d.datetime));
        }
    }
    extendOptions(ext = {}) {
        let defaultOptions = {
            intervalMinWidth: 8, // px
            tip: undefined,
            textTruncateThreshold: 30,
            enableLiveTimer: false,
            timerTickInterval: 1000,
            hideGroupLabels: false
        };
        Object.keys(ext).map(k => defaultOptions[k] = ext[k]);
        return defaultOptions;
    }
    getPointMinDt(p) {
        return p.longStartDate;
    }
    getPointMaxDt(p) {
        return p.longEndDate;
    }
    onVizChange(fn) {
        this.onVizChangeFn = fn;
        return this;
    }
}

module.exports = Timeline;
