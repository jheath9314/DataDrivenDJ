<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
    <meta name="description" content="">
    <meta name="author" content="">
    <link rel="icon" href="../../favicon.ico">

    <title>Data Driven DJ</title>

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>

    <!-- Latest compiled and minified CSS -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u"
        crossorigin="anonymous">

    <!-- Optional theme -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap-theme.min.css" integrity="sha384-rHyoN1iRsVXV4nD0JutlnGaslCJuC7uwjduW9SVrLvRYooPp2bWYgmgJQIXwl/Sp"
        crossorigin="anonymous">

    <!-- Latest compiled and minified JavaScript -->
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js" integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa"
        crossorigin="anonymous"></script>


    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js"></script>
      <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->

    <asset:stylesheet src="styles.css" />

    <asset:javascript src="app.js"/>
    <asset:javascript src="artist.js"/>

</head>

<body>

    <!-- Fixed navbar -->
    <nav class="navbar navbar-default navbar-fixed-top">
        <div class="container">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false"
                    aria-controls="navbar">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
                <a class="navbar-brand" href="index.html">Data Driven DJ</a>
            </div>
            <div id="navbar" class="navbar-collapse collapse">
                <ul class="nav navbar-nav">
                    <li><a href="index.html">Home</a></li>
                    <li><a href="browse.html">Browse</a></li>
                    <li><a href="search.html">Search</a></li>
                </ul>
            </div>
            <!--/.nav-collapse -->
        </div>
    </nav>

    <div class="container" role="main">

        <div class="page-header">
            <h3 id="artistNameAlbums"> </h3>
        </div>

        <div class="row">
            <div id="albums" class="col-sm-12">
                Loading...
            </div>
        </div>

        <div class="page-header">
            <h3 id="artistNameTerms"> </h3>
        </div>

        <div class="row">
            <div>

                <!-- Nav tabs -->
                <ul id="myTabs" class="nav nav-tabs" role="tablist">
                    <li role="presentation" class="active"><a href="#tf" aria-controls="tf" role="tab" data-toggle="tab">Raw Frequency</a></li>
                    <li role="presentation"><a href="#tfidf" aria-controls="tfidf" role="tab" data-toggle="tab">TF-IDF</a></li>
                </ul>

                <!-- Tab panes -->
                <div class="tab-content">
                    <div role="tabpanel" class="tab-pane active" id="tf">
                        <div id="tfWordCloud" class="col-sm-12">
                            Loading...
                        </div>
                    </div>
                    <div role="tabpanel" class="tab-pane" id="tfidf">
                        <div id="tfidfWordCloud" class="col-sm-12">
                            Loading...
                        </div>
                    </div>
                </div>

            </div>

        </div>

        <div class="page-header">
            <h3 id="artistNameTags"> </h3>
        </div>

        <div class="row">
            <div>

                <!-- Tab panes -->
                <div class="tab-content">
                    <div role="tabpanel" class="tab-pane active" id="tf">
                        <div id="tags" class="col-sm-12">
                            Loading...
                        </div>
                    </div>
                </div>

            </div>

        </div>
    </div>

</body>

</html>