module com.erdouglass.emdb.media {
    requires jakarta.validation;
    requires com.fasterxml.jackson.annotation;
    
    requires transitive com.erdouglass.common.validation;
    requires transitive com.erdouglass.emdb.common;

    exports com.erdouglass.emdb.media;
    exports com.erdouglass.emdb.media.credit;
    exports com.erdouglass.emdb.media.movie;
    exports com.erdouglass.emdb.media.person;
    exports com.erdouglass.emdb.media.series;
    exports com.erdouglass.emdb.media.show;
}