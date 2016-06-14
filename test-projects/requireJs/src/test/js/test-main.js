'use strict';

// -- RequireJS config --
requirejs.config({
    baseUrl: '../../',
    paths: {
        'app': ['src/main'],
        'test': ['src/test']
    },
    deps: [
        'test/namespace.test',
    ],
    callback: function(htmlUnit){
		jasmine.getEnv().execute();
    }
});

requirejs.onError = function(err) {
    console.error(err);
};
