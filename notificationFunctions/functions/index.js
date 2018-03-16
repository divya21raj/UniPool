'use strict'

const functions = require('firebase-functions');

const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

var body;

exports.sendNotification = functions.database.ref('/notifications/{user_id}/{notification_id}')
	.onWrite(event =>
	{
		const user_id = event.params.user_id;
		const notification_id = event.params.notification_id;

		console.log('We have a notif to send to : ', user_id);

		if(!event.data.val())
		{
			return console.log('A notif has been deleted from the DB : ', notification_id);
		}

		const fromUser = admin.database().ref(`/notifications/${user_id}/${notification_id}`).once('value');
		return fromUser.then(fromUserResult =>
		{
			const from_user_id = fromUserResult.val().from;
			var notif_type = fromUserResult.val().type;

			console.log('New notif from : ', from_user_id);
			console.log('Notification of type : ', notif_type);

			const userQuery = admin.database().ref(`users/${from_user_id}/name`).once('value');
			return userQuery.then(userResult =>
			{
				const userName = userResult.val();

				switch (notif_type)
				{
					case "requestAccepted":
						body = `${userName} has accepted your request.`;
						console.log('Sure is!');
						break;

					case "requestCreated":
						body = `${userName} has sent your request.`;
						console.log('Sure is!');
						break;

					default: break;
				}

				const deviceToken = admin.database().ref(`/users/${user_id}/deviceToken`).once('value');

				return deviceToken.then(result =>
				{
					const token_id = result.val();

					const payload =
					{
						notification :
						{
							title : "UniPool",
							body : `${body}`,
							icon : "default",
							sound : "default",
							click_action : "android.intent.action.TARGET_NOTIFICATION"
						}
					};

					return admin.messaging().sendToDevice(token_id, payload).then(response =>
					{
						console.log('This was the notifications feature!');
						return 1;
					});

				});

			});

		});

	});
