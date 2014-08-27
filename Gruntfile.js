'use strict';

var proxySnippet = require('grunt-connect-proxy/lib/utils').proxyRequest;

module.exports = function (grunt) {
    require('load-grunt-tasks')(grunt);
    require('time-grunt')(grunt);

    grunt.initConfig({
        yeoman: {
            // configurable paths
            app: require('./bower.json').appPath || 'src/main/webapp',
            dist: 'src/main/webapp/build/dist',
            dev: 'src/main/webapp/build/dev'
        },
        clean: {
            dist: {
                files: [
                    {
                        dot: true,
                        src: [
                            '.tmp',
                            '<%= yeoman.dist %>/*'
                        ]
                    }
                ]
            },
            dev:{
                files: [
                    {
                        dot: true,
                        src: [
                            '.tmp',
                            '<%= yeoman.dev %>'
                        ]
                    }
                ]
            },
            default: ['.tmp']
        },
        sync: {
            dist: {
                files: [
                    {
                        cwd: '<%= yeoman.app %>',
                        dest: '<%= yeoman.dist %>',
                        src: '**'
                    }
                ]
            }
        },
        less: {
            dev: {
/*                options: {
                    paths: ['<%= yeoman.app %>/less/']
                },*/
                files: {
                    '<%= yeoman.app %>/css/app.css': '<%= yeoman.app %>/less/app.less',
                    '<%= yeoman.app %>/css/flat.css': '<%= yeoman.app %>/less/flat-ui-pro.less'
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
                ],
                tasks: ['refresh']
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
                hostname: '0.0.0.0',
                livereload: 35729
            }
            //},
            ,
            livereload: {
                options: {
                    open: true,
                    base: [
                        '<%= yeoman.dev %>'
                    ],
                    middleware: function (connect) {
                        return [
                            proxySnippet,
                            modRewrite(['!\\.html|\\.js|\\.svg|\\.css|\\.png|\\.gif\\.jpg$ /index.html [L]']),
                            connect.static(require('path').resolve('src/main/webapp/build/dev'))
                        ];
                    }
                }
            },
            dist: {
                options: {
                    base: '<%= yeoman.dist %>',
                    middleware: function (connect) {
                        return [
                            proxySnippet,
                            modRewrite(['!\\.html|\\.js|\\.svg|\\.css|\\.png|\\.gif\\.jpg$ /index.html [L]']),
                            connect.static(require('path').resolve('src/main/webapp/build/dist'))
                        ];
                    }
                }
            }


        },
        // Put files not handled in other tasks here
        copy: {
            dist: {
                files: [
                    {
                        expand: true,
                        cwd: '<%= yeoman.app %>',
                        src: [
                            '**/views',
                            '**/images',
                            '**/fonts',
                            'index.html'],
                        dest: '<%= yeoman.dist %>/'
                    }
                ]
            },
            dev: {
                files: [
                    {
                        expand: true,
                        cwd: '<%= yeoman.app %>',
                        src: [
                            'views/**',
                            'images/**',
                            'fonts/**',
                            'js/dev/**',
                            'css/dev/**',
                            'index.html'],
                        dest: '<%= yeoman.dev %>/'
                    }
                ]
            }
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
        wiredep: {
            target: {
                src: '<%= yeoman.app %>/index.html'
            },
            cwd: '<%= yeoman.app %>',
            exclude:{
                src:['<%= yeoman.app %>/lib/bootstrap-css/js/bootstrap.min.js']
        }
        },
        useminPrepare: {
            dev: {
                src:['<%= yeoman.app %>/index.html'],
                options: {
                    root:'<%= yeoman.app %>',
                    dest: '<%= yeoman.dev %>'
                }
            },
            dist: {
                src:['<%= yeoman.app %>/index.html'],
                options: {
                    dest: '<%= yeoman.dist %>'
                }
            }
        },
        usemin:{
            'dev-html':{
                options:{
                    assetsDirs:['<%= yeoman.dev %>'],
                    type:'html'
                },
                files:{
                    src:['<%= yeoman.dev %>/index.html']
                }
            },
            'dev-css':{
                options:{
                    assetsDirs:['<%= yeoman.dev %>'],
                    type:'css'
                },
                files:{
                    src:['<%= yeoman.dev %>/styles/**.css']
                }
            }
        },
        ngAnnotate: {
            dev: {
                files: [
                    {
                        expand: true,
                        cwd: '<%= yeoman.app %>/js/',
                        src: ['**/*.js', '!**/gtresources/**'],
                        dest: '<%= yeoman.dev %>/js/'
                    }
                ]
            },
            dist: {
                files: [
                    {
                        expand: true,
                        cwd: '<%= yeoman.app %>/js/',
                        src: ['**/*.js', '!**/gtresources/**'],
                        dest: '<%= yeoman.dist %>/js/'
                    }
                ]
            }
        }
    });
    var modRewrite = require('connect-modrewrite');

    grunt.registerTask('server', function (target) {
        if (target === 'dev') {
            return  grunt.task.run([
                'clean:dev',
                'less:dev',
                'wiredep',
                'ngAnnotate:dev',
                'useminPrepare:dev',
                'concat:generated',
                'cssmin:generated',
                'uglify:generated',
                'copy:dev',
                'usemin',
                'configureProxies',
                'connect:livereload',
                'watch'
            ]);
        }
        if (target === 'dist') {
            return  grunt.task.run([
                'less:dev',
                'copy:dist',
                'wiredep',
                'ngAnnotate:dist',
                'useminPrepare:dist',
                'configureProxies',
                'connect:dist',
                'watch'
            ]);
        }
    });
    grunt.registerTask('server:dev', function (target) {
            return  grunt.task.run([
                'clean:dev',
                'less:dev',
                'wiredep',
                'ngAnnotate:dev',
                'useminPrepare:dev',
                'concat:generated',
                'cssmin:generated',
                'uglify:generated',
                'copy:dev',
                'usemin',
                'configureProxies',
                'connect:livereload',
                'watch'
            ]);
    });
    //TODO make this more efficient
    grunt.registerTask('refresh', function (target) {
        grunt.task.run([
            'less:dev',
            'copy:dev',
            'ngAnnotate:dev',
            'useminPrepare:dev',
            'concat:generated',
            'cssmin:generated',
            'usemin'
        ])
    })
};
