/* 
 * To change this template, choose Tools | Templates
 * http://www.joezimjs.com/javascript/javascript-design-patterns-observer/
 */

var JDY = JDY || {};
JDY.notify = JDY.notify || {};

JDY.notify.PersistentObservable = function() {
    this.subscribers = [];
};

JDY.notify.PersistentObservable.prototype = {
    
    subscribe: function(aPersistentListener) { 
	// Just add the callback to the subscribers list 
	this.subscribers.push(aPersistentListener);
    }, 
    unsubscribe: function(aPersistentListener) {
	var i = 0, len = this.subscribers.length;
	// Iterate through the array and if the callback is 
	// // found, remove it from the list of subscribers. 
	for (; i < len; i++) {
	    if (this.subscribers[i] === aPersistentListener) {
		this.subscribers.splice(i, 1);
		// Once we've found it, we don't need to 
		// // continue, so just return. 
		return;
	    }
	}
    }, 
    publish: function(aEvent) {
	var i = 0, len = this.subscribers.length;
	// Iterate over the subscribers array and call each of 
	// the callback functions. 
	for (; i < len; i++) {
	    this.subscribers[i].persistentStateChanged(aEvent);
	}
    }
};

//// The observer 
JDY.notify.PersistentListener = function() {
};

JDY.notify.PersistentListener.prototype.persistentStateChanged = function(aEvent) {
};

JDY.notify.ModificationType = {};
JDY.notify.ModificationType.CREATED = "CREATED";
JDY.notify.ModificationType.MODIFIED = "MODIFIED";
JDY.notify.ModificationType.DELETED = "DELETED";

JDY.notify.PersistentEvent = function() {
    var classInfo
	,changedObject
	,modificationType;

};





//// Here's where it gets used. 
observable = new Observable();
observable.subscribe(Observer);
observable.publish('We published!');
//// 'We published!' will be logged in the console 
observable.unsubscribe(Observer);
observable.publish('Another publish!');
// Nothing happens because there are no longer any subscribed observers