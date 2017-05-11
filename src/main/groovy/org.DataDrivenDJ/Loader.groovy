package org.DataDrivenDJ

import groovy.sql.GroovyRowResult
import groovy.sql.Sql

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

class Loader {

    public static void main(String[] args) {
        // connect to db
        Sql sql = Sql.newInstance("jdbc:mysql://aak17fpyksntg1.cev4izjnvwkq.us-west-2.rds.amazonaws.com/datadrivendj",
                "DataDjUser", 'HIDDEN', "com.mysql.jdbc.TopicModelGenerator");
        // execute a simple query


       List<String> rows = sql.rows("SELECT lyrics from song, Album where Album.ArtistId=? " +
                "and song.albumId=Album.AlbumId and lyrics is not null", ["22"])
        .collect({GroovyRowResult row -> row.lyrics})

        WordCloudBuilder wordCloudBuilder = new WordCloudBuilder();
        wordCloudBuilder.buildTF(rows).take(20).each({ println (it)})

        // close the connection
        sql.close();
    }

}
