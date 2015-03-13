Description

This program aims to provide the support to identify geonames for the unstructured text data in the project NSF polar research. https://github.com/NSF-Polar-Cyberinfrastructure/datavis-hackathon/issues/1

This project is a content-based geotagging solution, made of a variaty of NLP tools. 
My workflow:

1 Plain text input is passed to my geoparser

2 Location names are extracted from the text using OpenNLP NER

3 Use NER outputs as input for LDA so that each document only tagged by the most probable location name. ( difference )

4 location extracted above, search the best GeoName object ( currently working )

```
All libs are under /libs
	including: Solrj, Mallet, OpenNLP, etc.
```
