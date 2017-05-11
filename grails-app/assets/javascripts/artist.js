var artist;
var artistId;
$(document).ready(function () {
    var params = getUrlVars();
    console.log(params);
    artist = params.name;
    artistId = params.id;
    $('#artistNameAlbums').html(params.name + '<small> Albums</small>');
    $('#artistNameTags').html(params.name + '<small> Tags</small>');
    $('#artistNameTerms').html(params.name + '<small> Terms</small>');
    getArtistAlbums(encodeURIComponent(params.name));
    getTags(params.id);
    getTermFrequencyWordCloud(params.id);
    getTermFrequencyInverseDocumentFrequencyWordCloud(params.id);


    $('#myTabs a').click(function (e) {
        e.preventDefault()
        $(this).tab('show')
    })

    $('#albums').on('click','.btnDelete', function(){
        var id = $(this).attr('id');
        $.ajax({
            'url': '/artist/deleteAlbum?id=' + id,
            'type': 'POST',
            'success': function(data){
                getArtistAlbums(encodeURIComponent(params.name));
            }
        });
    });
});

function getArtistAlbums(params){
    $.ajax({
        'url': '/artist/findAlbumsByArtistJson?name=' + params,
        'type': 'GET',
        'success': function (data) {
            console.log(data);
            var html = '';
            for(var i = 0; i < data.albums.length; i++){
                html += '<div class="row"><div class="col-sm-12"><a id="' + data.albums[i].AlbumId + '" class="btnDelete btn-link "><span class="glyphicon glyphicon-trash"></span></a>&nbsp;&nbsp;&nbsp;<a href="album.html?id=' + data.albums[i].AlbumId + '&name=' + data.albums[i].Name + '&artist=' + artist + '&artistId=' + artistId + '">' + data.albums[i].Name + '</a></div></div>';
            }
            $('#albums').html(html);
        }
    });
}


function getTermFrequencyWordCloud(params){
    $.ajax({
        'url': '/artist/wordCloudJson?artistId=' + params,
        'type': 'GET',
        'success': function (data) {
            console.log(data);
            var sorted = sortObject(data);
            var length = sorted.length > 50? 50 : sorted.length;
            var html = '';
            for ( var i = 0; i < length; i++){
                html += '<span style="font-size:' + (sorted[i].value + 10) + 'px">' + sorted[i].key + ' </span>'
            }
            $('#tfWordCloud').html(html);
        }
    });
}

function getTermFrequencyInverseDocumentFrequencyWordCloud(params){
    $.ajax({
        'url': '/artist/tfIdfWordCloudJson?artistId=' + params,
        'type': 'GET',
        'success': function (data) {
            console.log(data);
            var sorted = sortObject(data);
            var length = sorted.length > 50? 50 : sorted.length;
            var html = '';
            for ( var i = 0; i < length; i++){
                html += '<span style="font-size:' + (sorted[i].value - 15) + 'px">' + sorted[i].key + ' </span>'
            }
            $('#tfidfWordCloud').html(html);
        }
    });
}

function getTags(params){
    $.ajax({
        'url': '/tag/topTagsArtist?artistId=' + params,
        'type': 'GET',
        'success': function (data) {
            console.log(data.tags);
            var html = '';
            for ( var i = 0; i < data.tags.length; i++){
                html += '<h4><span class="label label-primary">' + data.tags[i].name  + '<span class="badge">' + data.tags[i].weight + ' </span></span></h4>'
            }
            $('#tags').html(html);
        }
    });
}

function sortObject(obj) {
    var arr = [];
    var prop;
    for (prop in obj) {
        if (obj.hasOwnProperty(prop)) {
            arr.push({
                'key': prop,
                'value': obj[prop]
            });
        }
    }
    arr.sort(function(a, b) {
        return b.value - a.value;
    });
    return arr;
}