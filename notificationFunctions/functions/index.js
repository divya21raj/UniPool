'use strict'

const functions = require('firebase-functions');

const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.sendNotification = functions.database.ref('/notifications/{user_id}/{notification_id}').onWrite(event =>
	{
		const user_id = event.params.user_id;
		const notification = event.params.notification;
		
		console.log('The userId is : ', user_id);
	});
