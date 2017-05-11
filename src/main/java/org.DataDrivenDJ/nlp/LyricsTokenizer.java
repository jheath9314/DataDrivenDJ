package org.DataDrivenDJ.nlp;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

public class LyricsTokenizer {
  private Set<String> stopWords;

  public LyricsTokenizer(Set<String> stopWords) {
    this.stopWords = stopWords;
  }

  public static LyricsTokenizer fromFile() throws IOException {
    InputStream dictionaryFile = LyricsTokenizer.class.getClassLoader().getResourceAsStream("stopwords.txt");

    Set<String> stopWords = IOUtils.readLines(dictionaryFile, Charset.defaultCharset())
            .stream()
            .map(LyricsTokenizer::removePunctuations)
            .map(String::toLowerCase)
            .collect(Collectors.toSet());

    return new LyricsTokenizer(stopWords);
  }


  public static LyricsTokenizer fromBuiltInWords() {
    Set<String> stopWords =
        Stream.of("a", "all", "am", "an", "and", "any", "are", "arent",
            "as", "at", "be", "because", "been", "to", "from", "by",
            "can", "cant", "do", "dont", "didnt", "did", "its", "you", "the", "i", "me", "in" , "he", "her", "so").collect(Collectors.toSet());

    return new LyricsTokenizer(stopWords);
  }


  public List<String> tokenize(String lyrics) {
    String[] words = StringUtils.split(removePunctuations(lyrics.toLowerCase()));

    return Stream.of(words).filter(word -> !stopWords.contains(word)).collect(Collectors.toList());
  }

  private static String removePunctuations(String s) {
    StringBuilder stringBuilder = new StringBuilder();
    for (Character c : s.toCharArray()) {
      if (Character.isLetterOrDigit(c) || Character.isWhitespace(c)) {
        stringBuilder.append(c);
      }
    }

    return stringBuilder.toString();
  }
}
