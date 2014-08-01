'use strict';

var proxySnippet = require('grunt-connect-proxy/lib/utils').proxyRequest;

module.exports = function (grunt) {
  require('load-grunt-tasks')(grunt);
  require('time-grunt')(grunt);

  grunt.initConfig({
    yeoman: {
      // configurable paths
      app: require('./bower.json').appPath || 'src/main/webapp',
      dist: ''
    },
    sync: {
      dist: {
        files: [{
          cwd: '<%= yeoman.app %>',
          dest: '<%= yeoman.dist %>',
          src: '**'
        }]
      }
    },
      less:{
          development:{
              options:{
                  paths:['<%= yeoman.app %>']
              },
              files: {
                  '<%= yeoman.app %>/css/app.css':'<%= yeoman.app %>/less/app.less'
              }
          }
      },
    watch: {
      options: {
        livereload: 35729
      },
      src: {
        files: [
            '<%= yeoman.app %>/less/**/*',
          '<%= yeoman.app %>/*.html',
          '<%= yeoman.app %>/css/**/*',
          '<%= yeoman.app %>/js/**/*',
          '<%= yeoman.app %>/views/**/*'
        ]
        //tasks: ['sync:dist']
      }
    },
    connect: {
        //server: {
            proxies: [
                {
                    context: '/api',
                    host: 'localhost',
                    port: 8080,
                    https: false,
                    changeOrigin: false
                }
            ],
            options: {
                port: 9000,
                // Change this to '0.0.0.0' to access the server from outside.
                hostname: 'localhost',
                livereload: 35729
            }
        //},
        ,
            livereload: {
                options: {
                    open: true,
                    base: [
                        '<%= yeoman.app %>'
                    ],
                    middleware: function (connect) {
                        return [
                            proxySnippet,
                            connect.static(require('path').resolve('src/main/webapp'))
                        ];
                    }
                }
            }
            /*
             dist: {
             options: {
             base: '<%= yeoman.dist %>'
             }
             }
             */

    },
    // Put files not handled in other tasks here
    copy: {
      dist: {
        files: [{
          expand: true,
          dot: true,
          cwd: '<%= yeoman.app %>',
          dest: '<%= yeoman.dist %>',
          src: '**'
        }]
      },
    },
    // Test settings
    karma: {
      unit: {
        configFile: 'test/config/karma.conf.js',
        singleRun: true
      }
    },
    bowercopy: {
      options: {
        destPrefix: '<%= yeoman.app %>'
      },
      test: {
        files: {
          'test/lib/angular-mocks': 'angular-mocks',
          'test/lib/angular-scenario': 'angular-scenario'
        }
      }
    },
    wiredep:{
        target:{
            src:'<%= yeoman.app %>/index.html'
        },
        cwd:'<%= yeoman.app %>'
    },
      useminPrepare:{
          html:'<%= yeoman.app %>/index.html',
          options:{
              dest:'<%= yeoman.app %>/public'
          }
      },
      usemin:{
          html:'<%= yeoman.app %>/index.html',
          css:'<%= yeoman.app %>/css/*.css',
          options:{
              assetsDir:['<%= yeoman.app %>/public']
          }
      }
  });

    grunt.loadNpmTasks('grunt-contrib-less');
    grunt.loadNpmTasks('grunt-wiredep');

  grunt.registerTask('server', function (target) {
    grunt.task.run([
      //'copy:dist',
        'wiredep',
        'less:development',
        //'usemin',
      //'configureProxies:server',
        'configureProxies',
      'connect:livereload',
      'watch'
    ]);
  });
};
