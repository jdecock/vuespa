import authenticationService from '@/services/authentication.ts';
import userService from '@/services/user.ts';

export const Api = {
	authentication: authenticationService,
	user: userService
};
