package userRepo;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
class UserController {

	private final UserRepository repository;

	UserController(UserRepository repository) {
		this.repository = repository;
	}


	@GetMapping("/users")
	List<User> all() {
		return repository.findAll();
	}

	@PostMapping("/users")
	User newUser(@RequestBody User user) {
		return repository.save(user);
	}


	@GetMapping("/users/{id}")
	User one(@PathVariable Long id) {

		return repository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
	}

	@PutMapping("/users/{id}")
	User updateUser(@RequestBody User newUser, @PathVariable Long id) {

		return repository.findById(id) //
				.map(user -> {
					user.setName(newUser.getName());
					user.setRole(newUser.getRole());
					return repository.save(user);
				}) //
				.orElseGet(() -> {
					newUser.setId(id);
					return repository.save(newUser);
				});
	}

	@DeleteMapping("/users/{id}")
	void deleteUser(@PathVariable Long id) {
		repository.deleteById(id);
	}
}
