import type { UserRole } from '@/types/userRole.ts';

export type UserInfo = {
	id?: number;
	name: string;
	email: string;
	plainTextPassword?: string;
	roles?: Array<UserRole>;
	disabled?: boolean;
	disabledNote?: string;
	creationDate?: Date;
	lastModifiedDate?: Date;
};
