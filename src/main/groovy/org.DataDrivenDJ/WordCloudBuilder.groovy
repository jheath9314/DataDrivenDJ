package org.DataDrivenDJ

import org.DataDrivenDJ.nlp.LyricsTokenizer

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

class WordCloudBuilder {
    LyricsTokenizer lyricsTokenizer = LyricsTokenizer.fromFile();

    Map<String, Integer> buildTF(List<String> rows) {
        WordFrequencyCounter wordFrequencyCounter = new WordFrequencyCounter();

        rows.each({String lyrics ->
            List<String> lyricWords = lyricsTokenizer.tokenize(lyrics);
            lyricWords.each {
                wordFrequencyCounter.addOccurrence(it)
            }
        })

        return wordFrequencyCounter.getResults();
    }

}

