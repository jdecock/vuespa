import authenticationService from '@/services/authenticationService.ts';
import userService from '@/services/userService.ts';

export const Api = {
	authentication: authenticationService,
	user: userService
};
