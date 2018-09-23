TopicExplorer
=============
TopicExplorer is a web-based topic model browser that helps non-technical users to analyze data. Data is typically a collection of text pieces like blog posts, book chapters, Wikipedia pages, articles in journals and newspapers. Without the need of any further input, a topic model learns a number of word lists that often can be interpreted as topics. TopicExplorer helps users to explore the semantics of the learned topics with several visual and interactive features. The ecosystem around the TopicExplorer browser include web applications to filter text corpora, tune the vocabulary used in the analysis and create new topic models.

#### [Read more about features and use cases in the blog](http://topicexplorer.informatik.uni-halle.de)

### Using TopicExplorer
[TopicExplorer-docker](https://github.com/hinneburg/TopicExplorer-docker) is a docker-compose project that allows simplified installation and configurations of all components of TopicExplorer on Linux, Windows and Mac. 

### Dependencies from other software packages
- [Mariadb](https://mariadb.org/) 10.0.x
  - necessary changes of mariadb (mysql) defaults
    - allow `load local infile`
This may not be neccessary in Ubuntu. You need to find maria(mysql)-server config file,
for Ubuntu this is at `/etc/mysql/my.cnf`. Insert `local-infile=1` into both sections
under `[mysqld]` and `[mysql]`.
    - set `innodb_buffer_pool_size` to a large size like `8GB`
    - set `ft_min_word_len=1` in case of Japanese words to allow fulltext search of small words
    - set `group_concat_max_len=1000000` to a large value to allow constucting Japanese documents from a table containing all tokenized words of each document. The parameter value needs to be an upper bound of the constructed document size.
- [R](http://www.r-project.org): make sure that `Rscript` command is in your general search path.
- [Apache Tomcat](http://tomcat.apache.org/)
- [TreeTagger](http://www.cis.uni-muenchen.de/~schmid/tools/TreeTagger/) for English and German and [Mecab](http://taku910.github.io/mecab/) for Japanese tokenization and lemmatization

