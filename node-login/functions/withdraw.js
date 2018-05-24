'use strict';

const user = require('../models/user');

exports.withdrawUser = email => 
	
	new Promise((resolve,reject) => {

		user.find({ email: email }, { name: 1, email: 1, created_at: 1, _id: 0 })

		.then(user => {

			if (bcrypt.compareSync(token, user.temp_password)) {

				user.remove({ email: email})

			} else {

				reject({ status: 401, message: 'Invalid Token !' });
			}
		})

		.then(user => resolve({ status: 200, message: 'Withdraw Sucessfully !' }))

		.catch(err => reject({ status: 500, message: 'Internal Server Error !' }))

	});


