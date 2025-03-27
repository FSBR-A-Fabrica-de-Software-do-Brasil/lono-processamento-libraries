package espe.lono.indexercore.log;

import espe.lono.indexercore.LonoIndexerConfigs;

/**
 * @corp ESPE
 * @author Petrus Augusto (R@g3)
 * @date 06/12/2017
 */
public class Logger {    
    public static void info(String message) {
        org.apache.log4j.Logger _logger = LonoIndexerConfigs.INDEXER_LOG4_LOGGER;
        if (_logger == null ) return;
        _logger.info(message);
    }
    public static void info(String message, Throwable t) {
        org.apache.log4j.Logger _logger = LonoIndexerConfigs.INDEXER_LOG4_LOGGER;
        if (_logger == null ) return;
        _logger.info(message, t);
    }
    
    public static void debug(String message) {
        org.apache.log4j.Logger _logger = LonoIndexerConfigs.INDEXER_LOG4_LOGGER;
        if (_logger == null ) return;
        _logger.debug(message);
    }
    public static void debug(String message, Throwable t) {
        org.apache.log4j.Logger _logger = LonoIndexerConfigs.INDEXER_LOG4_LOGGER;
        if (_logger == null ) return;
        _logger.debug(message, t);
    }
    
    public static void fatal(String message) {
        org.apache.log4j.Logger _logger = LonoIndexerConfigs.INDEXER_LOG4_LOGGER;
        if (_logger == null ) return;
        _logger.fatal(message);
    }
    public static void fatal(String message, Throwable t) {
        org.apache.log4j.Logger _logger = LonoIndexerConfigs.INDEXER_LOG4_LOGGER;
        if (_logger == null ) return;
        _logger.fatal(message, t);
    }
}
