<template>
	<main>
		<h1>Sign Up</h1>

		<a href="#">Sign up with Google</a>

		<div>or sign in with email</div>

		<form>
			<div>
				<label for="inputName">Name</label><br />
				<input type="text" id="inputName" />
			</div>

			<div>
				<label for="inputEmail">Email</label><br />
				<input type="email" id="inputEmail" />
			</div>

			<div>
				<label for="inputPassword">Password</label><br />
				<input type="password" id="inputPassword" />
			</div>

			<div>
				<input type="checkbox" id="checkboxTerms" />
				<label for="checkboxTerms">I agree with the <a href="#">Terms of Service</a>.</label>
			</div>

			<div>
				<button type="submit">Create Account</button>
			</div>
		</form>

		Already have an account? <RouterLink to="/sign-in">Sign In</RouterLink>
	</main>
</template>
