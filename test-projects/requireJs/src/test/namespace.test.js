define(['app/namespace'], function() {
	describe("A suite", function() {
	  it("contains spec with an expectation", function() {
	    expect(true).toBe(false);
	  });

	  it("validates that namespace is correct", function(){
	    expect(namespace).toBe('some_namespace');
	  });

	});
});