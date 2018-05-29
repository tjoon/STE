'use strict';

const user = require('../models/user');

exports.withdrawUser = (email, currentPassword, confirmPassword) => 
	
	new Promise((resolve,reject) => {
		user.find({ email: email })

		.then(users => {
			var _id = users[0]._id

			if(currentPassword == confirmPassword){
				user.remove({ _id: _id}, function(err, output){
					if(err){
						console.log("@#$#@$#$@#$ remove fail !")
						reject(Error("@#$#@$#@ remove fail"))
	
					} else {
	
						console.log("@#$#@$#$@#$ resolve !")
						resolve({ status: 200, message: 'Withdraw Successfully !' })
	
					}
				})
			} else {
				reject({ status: 401, message: 'Invalid Password Confirm !' });
			}
			/*
			user.remove({ _id: _id}, function(err, output){

				if(err){
					console.log("@#$#@$#$@#$ remove fail !")
					reject(Error("@#$#@$#@ remove fail"))

				} else {

					console.log("@#$#@$#$@#$ resolve !")
					resolve({ status: 200, message: 'Withdraw Successfully !' })

				}
			})
			*/
		})
		

		.catch(err => reject({ status: 500, message: 'Internal Server Error !' }));
	})