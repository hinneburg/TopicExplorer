package cc.topicexplorer.plugin.mecab.initcorpus.command;

import java.sql.SQLException;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.common.collect.Sets;

import cc.topicexplorer.commands.TableFillCommand;

public class PosTypeFill extends TableFillCommand {
	private static final Logger logger = Logger.getLogger(PosTypeFill.class);

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet("PosTypeCreate");
	}

	@Override
	public Set<String> getOptionalAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getOptionalBeforeDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public void fillTable() {
		String textAnalyzer = properties.getProperty("Mecab_text-analyzer").trim();

		try {
			if ("mecab".equals(textAnalyzer)) {
			this.database.executeUpdateQuery("INSERT INTO " + this.tableName
					+ " (`POS`, `LOW`, `HIGH`, `DESCRIPTION`, `PARENT_POS`) VALUES "
					+ "(0, 1000, 1999, '間投', 100),"
					+ "(1, 2000, 2999, 'フィラー', -1),"
					+ "(2, 3000, 3999, '感動詞', -1),"
					+ "(3, 5000, 5999, 'アルファベット', 101),"
					+ "(4, 6000, 6999, '一般', 101),"
					+ "(5, 7000, 7999, '括弧開', 101),"
					+ "(6, 8000, 8999, '括弧閉', 101),"
					+ "(7, 9000, 9999, '句点', 101),"
					+ "(8, 10000, 10999, '空白', 101),"
					+ "(9, 11000, 11999, '読点', 101),"
					+ "(10, 13000, 13999, '自立', 102),"
					+ "(11, 14000, 14999, '接尾', 102),"
					+ "(12, 15000, 15999, '非自立', 102),"
					+ "(13, 18000, 18999, '一般', 104),"
					+ "(14, 19000, 19999, '引用', 104),"
					+ "(15, 20000, 20999, '連語', 104),"
					+ "(16, 21000, 21999, '係助詞', 103),"
					+ "(17, 22000, 22999, '終助詞', 103),"
					+ "(18, 23000, 23999, '接続助詞', 103),"
					+ "(19, 24000, 24999, '特殊', 103),"
					+ "(20, 25000, 25999, '副詞化', 103),"
					+ "(21, 26000, 26999, '副助詞', 103),"
					+ "(22, 27000, 27999, '副助詞／並立助詞／終助詞', 103),"
					+ "(23, 28000, 28999, '並立助詞', 103),"
					+ "(24, 29000, 29999, '連体化', 103),"
					+ "(25, 30000, 30999, '助動詞', -1),"
					+ "(26, 31000, 31999, '接続詞', -1),"
					+ "(27, 33000, 33999, '形容詞接続', 105),"
					+ "(28, 34000, 34999, '数接続', 105),"
					+ "(29, 35000, 35999, '動詞接続', 105),"
					+ "(30, 36000, 36999, '名詞接続', 105),"
					+ "(31, 38000, 38999, '自立', 106),"
					+ "(32, 39000, 39999, '接尾', 106),"
					+ "(33, 40000, 40999, '非自立', 106),"
					+ "(34, 42000, 42999, '一般', 107),"
					+ "(35, 43000, 43999, '助詞類接続', 107),"
					+ "(36, 45000, 45999, 'サ変接続', 108),"
					+ "(37, 46000, 46999, 'ナイ形容詞語幹', 108),"
					+ "(38, 47000, 47999, '一般', 108),"
					+ "(39, 48000, 48999, '引用文字列', 108),"
					+ "(40, 49000, 49999, '形容動詞語幹', 108),"
					+ "(41, 51000, 51999, '一般', 109),"
					+ "(42, 53000, 53999, '一般', 110),"
					+ "(43, 54000, 54999, '姓', 110),"
					+ "(44, 55000, 55999, '名', 110),"
					+ "(45, 56000, 56999, '組織', 109),"
					+ "(46, 58000, 58999, '一般', 111),"
					+ "(47, 59000, 59999, '国', 111),"
					+ "(48, 60000, 60999, '数', 108),"
					+ "(49, 61000, 61999, '接続詞的', 108),"
					+ "(50, 63000, 63999, 'サ変接続', 112),"
					+ "(51, 64000, 64999, '一般', 112),"
					+ "(52, 65000, 65999, '形容動詞語幹', 112),"
					+ "(53, 66000, 66999, '助数詞', 112),"
					+ "(54, 67000, 67999, '助動詞語幹', 112),"
					+ "(55, 68000, 68999, '人名', 112),"
					+ "(56, 69000, 69999, '地域', 112),"
					+ "(57, 70000, 70999, '特殊', 112),"
					+ "(58, 71000, 71999, '副詞可能', 112),"
					+ "(59, 73000, 73999, '一般', 113),"
					+ "(60, 74000, 74999, '縮約', 113),"
					+ "(61, 75000, 75999, '動詞非自立的', 108),"
					+ "(62, 77000, 77999, '助動詞語幹', 114),"
					+ "(63, 79000, 79999, '一般', 115),"
					+ "(64, 80000, 80999, '形容動詞語幹', 115),"
					+ "(65, 81000, 81999, '助動詞語幹', 115),"
					+ "(66, 82000, 82999, '副詞可能', 115),"
					+ "(67, 83000, 83999, '副詞可能', 108),"
					+ "(68, 84000, 84999, '連体詞', -1),"
					+ "(100, 0, 1999, 'その他', -1),"
					+ "(101, 4000, 11999, '記号', -1),"
					+ "(102, 12000, 15999, '形容詞', -1),"
					+ "(103, 16000, 29999, '助詞', -1),"
					+ "(104, 17000, 20999, '格助詞', 103),"
					+ "(105, 32000, 36999, '接頭詞', -1),"
					+ "(106, 37000, 40999, '動詞', -1),"
					+ "(107, 41000, 43999, '副詞', -1),"
					+ "(108, 44000, 83999, '名詞', -1),"
					+ "(109, 50000, 59999, '固有名詞', 108),"
					+ "(110, 52000, 55999, '人名', 109),"
					+ "(111, 57000, 59999, '地域', 109),"
					+ "(112, 62000, 71999, '接尾', 108),"
					+ "(113, 72000, 74999, '代名詞', 108),"
					+ "(114, 76000, 77999, '特殊', 108),"
					+ "(115, 78000, 82999, '非自立', 108)");
			} else if ("treetagger".equals(textAnalyzer)) {
				String treeTaggerModel = properties.getProperty("Mecab_treetagger-model").trim();
				if ("/german-utf8.par".equals(treeTaggerModel)) {
					this.database.executeUpdateQuery("INSERT INTO POS_TYPE"+
							"(`POS`, `LOW`, `HIGH`, `DESCRIPTION`, `LONG_DESCRIPTION`, `PARENT_POS`) VALUES "+
							"(0, 0, 999, 'N', 'Nomen', -1),"+
							"(113, 0, 99, 'NN', 'normales Nomen', 0),"+
							"(114, 100, 199, 'NE', 'Eigennamen', 0),"+
							"(1, 1000, 1999, 'V', 'Verben', -1),"+
							"(135, 1000, 1099, 'VV', 'Verben voll', 1),"+
							"(138, 1000, 1009, 'VVFIN', 'finites Verb, voll', 135),"+
							"(139, 1010, 1019, 'VVIMP', 'Imperativ, voll', 135),"+
							"(140, 1020, 1029, 'VVINF', 'Infinitiv, voll', 135),"+
							"(141, 1030, 1039, 'VVIZU', 'Infinitiv mit zu, voll', 135),"+
							"(142, 1040, 1049, 'VVPP', 'Partizip Perfekt, voll', 135),"+
							"(136, 1100, 1199, 'VA', 'Verben aux', 1),"+
							"(143, 1100, 1109, 'VAFIN', 'finites Verb, aux', 136),"+
							"(144, 1110, 1119, 'VAIMP', 'Imperativ, aux', 136),"+
							"(145, 1120, 1129, 'VAINF', 'Infinitiv, aux', 136),"+
							"(147, 1130, 1139, 'VAPP', 'Partizip Perfekt, aux', 136),"+
							"(137, 1200, 1299, 'VM', 'Verben modal', 1),"+
							"(148, 1200, 1209, 'VMFIN', 'finites Verb, modal', 137),"+
							"(149, 1210, 1219, 'VMINF', 'Infinitiv, modal', 137),"+
							"(150, 1220, 1229, 'VMPP', 'Partizip Perfekt, modal', 137),"+
							"(2, 2000, 2999, 'ART', 'Artikel', -1),"+
							"(3, 3000, 3999, 'ADJ', 'Adjektive', -1),"+
							"(100, 3000, 3499, 'ADJA', 'attributives Adjektiv', 3),"+
							"(101, 3500, 3999, 'ADJD', 'adverbiales oder pradikatives Adjektiv', 3),"+
							"(4, 4000, 4999, 'P', 'Pronomina', -1),"+
							"(115, 4010, 4019, 'PDS', 'substituierendes Demonstrativpronomen', 4),"+
							"(116, 4020, 4029, 'PDAT', 'attribuierendes Demonstrativpronomen', 4),"+
							"(117, 4030, 4039, 'PIS', 'substituierendes Indefinitpronomen', 4),"+
							"(118, 4040, 4049, 'PIAT', 'attribuierendes Indefinitpronomen ohne Determiner', 4),"+
							"(119, 4050, 4059, 'PIDAT', 'attribuierendes Indefinitpronomen mit Determiner', 4),"+
							"(120, 4060, 4069, 'PPER', 'irreflexives Personalpronomen', 4),"+
							"(121, 4070, 4079, 'PPOSS', 'substituierendes Possessivpronomen', 4),"+
							"(122, 4080, 4089, 'PPOSAT', 'attribuierendes Possessivpronomen', 4),"+
							"(123, 4090, 4099, 'PRELS', 'Relativpronomen substituierend', 4),"+
							"(124, 4100, 4109, 'PRELAT', 'Relativpronomen attribuierend', 4),"+
							"(125, 4110, 4119, 'PRF', 'reflexives Personalpronomen', 4),"+
							"(126, 4120, 4129, 'PWS', 'substituierendes Interrogativpronomen', 4),"+
							"(127, 4130, 4139, 'PWAT', 'attribuierendes Interrogativpronomen', 4),"+
							"(128, 4140, 4149, 'PWAV', 'adverbiales Interrogativ- oder Relativpronomen', 4),"+
							"(129, 4150, 4159, 'PAV', 'Pronominaladverb', 4),"+
							"(5, 5000, 5999, 'CARD', 'Kardinalzahlen', -1),"+
							"(6, 6000, 6999, 'ADV', 'Adverbien', -1),"+
							"(7, 7000, 7999, 'KO', 'Konjunktionen', -1),"+
							"(109, 7000, 7099, 'KOUI', 'unterordnende Konjunktion mit zu und Infinitiv', 7),"+
							"(110, 7100, 7199, 'KOUS', 'unterordnende Konjunktion mit Satz', 7),"+
							"(111, 7200, 7299, 'KON', 'nebenordnende Konjunktion', 7),"+
							"(112, 7300, 7399, 'KOKOM', 'Vergleichspartikel, ohne Satz', 7),"+
							"(8, 8000, 8999, 'AP', 'Adpositionen', -1),"+
							"(102, 8000, 8099, 'APPR', 'Praposition; Zirkumposition links',8),"+
							"(103, 8100, 8199, 'APPRART', 'Praposition mit Artikel',8),"+
							"(104, 8200, 8299, 'APPO', 'Postposition',8),"+
							"(105, 8300, 8399, 'APZR', 'Zirkumposition rechts',8),"+
							"(9, 9000, 9999, 'ITJ', 'Interjektionen', -1),"+
							"(10, 10000, 10999, 'PTK', 'Partikeln', -1),"+
							"(130, 10000, 10099, 'PTKZU', 'zu vor Infinitiv', 10),"+
							"(131, 10100, 10199, 'PTKNEG', 'Negationspartikel', 10),"+
							"(132, 10200, 10299, 'PTKVZ', 'abgetrennter Verbzusatz', 10),"+
							"(133, 10300, 10399, 'PTKANT', 'Antwortpartikel', 10),"+
							"(134, 10400, 10499, 'PTKA', 'Partikel bei Adjektiv oder Adverb', 10),"+
							"(11, 11000, 11999, 'FM', 'Fremdsprachliches Material', -1),"+
							"(12, 12000, 12999, 'TRUNC', 'KompositionsErstglieder', -1),"+
							"(13, 13000, 13999, 'XY', 'Nichtworter', -1),"+
							"(14, 14000, 14999, '$,', 'Komma', -1),"+
							"(15, 15000, 15999, '$.', 'Satzende', -1),"+
							"(16, 16000, 16999, '$(', 'Sonstiges Satzzeichen', -1)"+
							";"
                      );
				} else if ("/english-utf8.par".equals(treeTaggerModel)) {
					this.database.executeUpdateQuery("INSERT INTO POS_TYPE"+
							"(POS,LOW,HIGH,DESCRIPTION,LONG_DESCRIPTION,PARENT_POS) VALUES "+
							"(1,1000,1999, 'CC', 'Coordinating conjunction',-1),"+
							"(2,2000,2999, 'CD', 'Cardinal number',-1),"+
							"(3,3000,3999, 'DT', 'Determiner',-1),"+
							"(4,4000,4999, 'EX', 'Existential there',-1),"+
							"(5,5000,5999, 'FW', 'Foreign word',-1),"+
							"(6,6000,6999, 'IN', 'Preposition or subordinating conjunction',-1),"+
							"(7,7000,7999, 'JJ', 'Adjective',-1),"+
							"(8,7100,7199, 'JJR', 'Adjective, comparative',7),"+
							"(9,7200,7299, 'JJS', 'Adjective, superlative',7),"+
							"(10,8000,1999, 'LS', 'List item marker',-1),"+
							"(11,9000,1999, 'MD', 'Modal verb',-1),"+
							"(12,10000,10999, 'N', 'Noun, general',-1),"+
							"(13,10000,10599, 'NO', 'Noun, but not proper noun',12),"+
							"(14,10000,10299, 'NN', 'Noun, singular or mass',13),"+
							"(15,10300,10399, 'NNS', 'Noun, plural',13),"+
							"(16,10600,10999, 'NOP', 'Proper noun, general',12),"+
							"(17,10600,10799, 'NP', 'Proper noun, singular',16),"+
							"(18,10800,10899, 'NPS', 'Proper noun, plural',16),"+
							"(19,11000,11999, 'PDT', 'Predeterminer',-1),"+
							"(20,12000,12999, 'POS', 'Possessive ending',-1),"+
							"(21,13000,13999, 'PP', 'Personal pronoun',-1),"+
							"(22,14000,14999, 'PP$', 'Possessive pronoun',-1),"+
							"(23,15000,15999, 'RB', 'Adverb',-1),"+
							"(24,15000,15599, 'RBR', 'Adverb, comparative',23),"+
							"(25,15600,15999, 'RBS', 'Adverb, superlative',23),"+
							"(26,16000,16999, 'RP', 'Particle',-1),"+
							"(27,17000,17999, 'SYM', 'Symbol',-1),"+
							"(28,18000,18999, 'TO', 'to',-1),"+
							"(29,19000,19999, 'UH', 'Interjection',-1),"+
							"(30,20000,20999, 'V', 'Verb, general',-1),"+
							"(31,20000,20009, 'VB', 'Verb be, base form',30),"+
							"(32,20010,20019, 'VBD', 'Verb be, past tense',30),"+
							"(33,20020,20029, 'VBG', 'Verb be, gerund or present participle',30),"+
							"(34,20030,20039, 'VBN', 'Verb be, past participle',30),"+
							"(35,20040,20049, 'VBP', 'Verb be, non-3rd person singular present',30),"+
							"(36,20050,20059, 'VBZ', 'Verb be, 3rd person singular present',30),"+
							"(37,20060,20069, 'VV', 'Verb, base form',30),"+
							"(38,20070,20079, 'VVD', 'Verb, past tense',30),"+
							"(39,20080,20089, 'VVG', 'Verb, gerund or present participle',30),"+
							"(40,20090,20099, 'VVN', 'Verb, past participle',30),"+
							"(41,20100,20109, 'VVP', 'Verb, non-3rd person singular present',30),"+
							"(42,20110,20119, 'VVZ', 'Verb, 3rd person singular present',30),"+
							"(43,20120,20129, 'VH', 'Verb have, base form',30),"+
							"(44,20130,20139, 'VHD', 'Verb have, past tense',30),"+
							"(45,20140,20149, 'VHG', 'Verb have, gerund or present participle',30),"+
							"(46,20150,20159, 'VHN', 'Verb have, past participle',30),"+
							"(47,20160,20169, 'VHP', 'Verb have, non-3rd person singular present',30),"+
							"(48,20170,20179, 'VHZ', 'Verb have, 3rd person singular present',30),"+
							"(49,21000,21999, 'WDT', 'Wh-determiner',-1),"+
							"(50,22000,22999, 'WP', 'Wh-pronoun',-1),"+
							"(51,23000,23999, 'WP$', 'Possessive wh-pronoun',-1),"+
							"(52,24000,24999, 'WRB', 'Wh-adverb',-1),"+ 
							"(53,25000,25999, 'IN/that', 'Preposition or subordinating conjunction',-1),"+
							"(54,26000,26999, ':', 'Unknown',-1);"
							);
//					VHZ
				}
				
				
			}
			
			} catch (SQLException e) {
			logger.error("Table " + this.tableName + " could not be filled.");
			throw new RuntimeException(e);
		}
		
	}

	@Override
	public void setTableName() {
		this.tableName = "POS_TYPE";
	}

}
