/* 
 * jdynameta, 
 * Copyright (c)2013 Rainer Schneider, Roggenburg.
 * Distributed under Apache 2.0 license
 * http://jdynameta.de
 */

var JDY = JDY || {};

JDY.app = {};

JDY.app.getRepositoryHandlers = function (reader) {
	
	var appRep = JDY.meta.createAppRepository();
	
	
	function loadTypeInfoFunc(aRepoName, aClassName, callback) {
		var repFilter = new JDY.base.QueryCreator(
					appRep.getClassInfo("AppRepository")).equal("applicationName",aRepoName).query(),
			newRepository;
			
		reader.loadValuesFromDb(repFilter, function(resultRepos){

			JDY.meta.convertAppRepositoryToRepository(resultRepos[0], function (newRepository) {
				callback(newRepository.getClassInfo(aClassName));
			});
		});
	};

	function loadAllReposFunc(callBack) {
		
		var allRepoFilter = new JDY.base.QueryCreator(appRep.getClassInfo("AppRepository")).query();
		reader.loadValuesFromDb(allRepoFilter, callBack);
	};

	return {
		handleAllRepos: loadAllReposFunc,
		loadTypeInfo: loadTypeInfoFunc
	};
};
